package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.LoginDto.LoginRequest;
import com.dal.asdc.reconnect.dto.SignUp.SignUpFirstPhaseBody;
import com.dal.asdc.reconnect.dto.SignUp.SignUpFirstPhaseRequest;
import com.dal.asdc.reconnect.dto.SignUp.SignUpSecondPhaseRequest;
import com.dal.asdc.reconnect.exception.*;
import com.dal.asdc.reconnect.model.*;
import com.dal.asdc.reconnect.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service class for handling user authentication and registration.
 * Provides methods for validating user input during registration,
 * adding new users, handling user authentication, and managing user skills.
 */
@Component
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern email_pattern = Pattern.compile(EMAIL_PATTERN);

    private final UserTypeRepository userTypeRepository;
    private final UsersSkillsRepository usersSkillsRepository;
    private final UsersRepository usersRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final CompanyRepository companyRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final SkillsRepository skillsRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Validates the email format.
     *
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean validateEmail(String email) {
        Matcher matcher = email_pattern.matcher(email);
        boolean isValid = matcher.matches();
        log.debug("Email validation for '{}': {}", email, isValid);
        return isValid;
    }

    /**
     * Validates the first phase of the user sign-up process.
     *
     * @param signUpFirstPhaseRequest the request containing user details for the first phase
     * @return a {@link SignUpFirstPhaseBody} object containing validation results
     * @throws UserAlreadyExistsException if the email is already registered
     * @throws PasswordMismatchException  if the passwords do not match
     * @throws InvalidEmailException      if the email format is invalid
     */
    public SignUpFirstPhaseBody validateFirstPhase(SignUpFirstPhaseRequest signUpFirstPhaseRequest) {
        SignUpFirstPhaseBody signUpFirstPhaseBody = new SignUpFirstPhaseBody();
        Users user = getUserByEmail(signUpFirstPhaseRequest.getEmail());

        if (user != null) {
            log.warn("Email '{}' is already present", signUpFirstPhaseRequest.getEmail());
            throw new UserAlreadyExistsException("Email is already present");
        }

        if (!matchPasswordWithConfirmPassword(signUpFirstPhaseRequest.getPassword(), signUpFirstPhaseRequest.getReenteredPassword())) {
            log.warn("Passwords do not match for email '{}'", signUpFirstPhaseRequest.getEmail());
            throw new PasswordMismatchException("Passwords do not match");
        }

        if (!validateEmail(signUpFirstPhaseRequest.getEmail())) {
            log.warn("Invalid email format for '{}'", signUpFirstPhaseRequest.getEmail());
            throw new InvalidEmailException("Invalid email format");
        }

        return signUpFirstPhaseBody;
    }

    /**
     * Compares the provided password with the confirmed password.
     *
     * @param password        the password to check
     * @param confirmPassword the password confirmation to compare
     * @return true if both passwords match, false otherwise
     */
    private boolean matchPasswordWithConfirmPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the email of the user to retrieve
     * @return the {@link Users} object if found, null otherwise
     */
    public Users getUserByEmail(String email) {
        Optional<Users> user = usersRepository.findByUserEmail(email);
        log.debug("User retrieval by email '{}': {}", email, user.isPresent());
        return user.orElse(null);
    }

    /**
     * Adds a new user to the system.
     *
     * @param signUpSecondPhaseRequest the request containing user details for the second phase
     * @param fileNameAndPath          the file path for the profile picture
     * @return true if the user was added successfully, false otherwise
     */
    @Transactional
    public boolean addNewUser(SignUpSecondPhaseRequest signUpSecondPhaseRequest, String fileNameAndPath) {
        try {
            UserDetails userDetails = addDetails(signUpSecondPhaseRequest, fileNameAndPath);
            if (userDetails == null) {
                return false;
            }

            Users user = addUser(signUpSecondPhaseRequest, userDetails);
            if (user == null) {
                return false;
            }

            if (!addSkills(signUpSecondPhaseRequest)) {
                return false;
            }
        } catch (EntityNotFoundException | SkillNotFoundException e) {
            log.error("Error occurred during user creation: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error occurred during user creation: {}", e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Validates the second phase of the user sign-up process.
     *
     * @param signUpSecondPhaseRequest the request containing user details for the second phase
     * @return true if validation is successful
     * @throws EntityNotFoundException if any required entities are not found
     * @throws SkillNotFoundException  if any skill IDs are not found
     */
    public boolean validateSecondPhase(SignUpSecondPhaseRequest signUpSecondPhaseRequest) {
        Optional<UserType> userType = userTypeRepository.findById(signUpSecondPhaseRequest.getUserType());
        Optional<Company> company = companyRepository.findById(signUpSecondPhaseRequest.getCompany());
        Optional<City> city = cityRepository.findById(signUpSecondPhaseRequest.getCity());
        Optional<Country> country = countryRepository.findById(signUpSecondPhaseRequest.getCountry());

        if (userType.isEmpty() || company.isEmpty() || city.isEmpty() || country.isEmpty()) {
            log.warn("Validation failed for second phase sign-up request: missing entities");
            throw new EntityNotFoundException("Required entity not found during validation");
        }

        for (Integer skillId : signUpSecondPhaseRequest.getSkills()) {
            Optional<Skills> skills = skillsRepository.findById(skillId);
            if (skills.isEmpty()) {
                log.warn("Skill ID '{}' not found during second phase sign-up validation", skillId);
                throw new SkillNotFoundException("Skill ID not found during validation");
            }
        }
        return true;
    }

    /**
     * Adds skills to a user.
     *
     * @param signUpSecondPhaseRequest the request containing user details for the second phase
     * @return true if skills were added successfully
     * @throws EntityNotFoundException if the user is not found
     * @throws SkillNotFoundException  if any skills are not found
     */
    public boolean addSkills(SignUpSecondPhaseRequest signUpSecondPhaseRequest) {
        Optional<Users> users = usersRepository.findByUserEmail(signUpSecondPhaseRequest.getEmail());
        if (users.isEmpty()) {
            throw new EntityNotFoundException("User not found by email: " + signUpSecondPhaseRequest.getEmail());
        }
        for (Integer skillId : signUpSecondPhaseRequest.getSkills()) {
            Optional<Skills> skills = skillsRepository.findById(skillId);
            if (skills.isEmpty()) {
                throw new SkillNotFoundException("Skill not found by ID: " + skillId);
            }
            UserSkills userSkills = new UserSkills();
            userSkills.setSkill(skills.get());
            userSkills.setUsers(users.get());
            usersSkillsRepository.save(userSkills);
        }
        return true;
    }

    /**
     * Adds user details to the system.
     *
     * @param signUpSecondPhaseRequest the request containing user details for the second phase
     * @param fileNameAndPath          the file path for the profile picture
     * @return the {@link UserDetails} object if saved successfully
     * @throws EntityNotFoundException if any required entities are not found
     */
    public UserDetails addDetails(SignUpSecondPhaseRequest signUpSecondPhaseRequest, String fileNameAndPath) {
        Optional<Company> company = companyRepository.findById(signUpSecondPhaseRequest.getCompany());
        Optional<City> city = cityRepository.findById(signUpSecondPhaseRequest.getCity());
        Optional<Country> country = countryRepository.findById(signUpSecondPhaseRequest.getCountry());

        if (company.isEmpty() || city.isEmpty() || country.isEmpty()) {
            log.error("Company, City, or Country not found during user details addition");
            throw new EntityNotFoundException("Required entity not found during user details addition");
        }

        UserDetails userDetails = new UserDetails();
        userDetails.setUserName(signUpSecondPhaseRequest.getUserName());
        userDetails.setCompany(company.get());
        userDetails.setExperience(signUpSecondPhaseRequest.getExperience());
        userDetails.setResume(signUpSecondPhaseRequest.getResume());
        userDetails.setProfilePicture(fileNameAndPath);
        userDetails.setCity(city.get());
        userDetails.setCountry(country.get());

        return userDetailsRepository.save(userDetails);
    }

    /**
     * Adds a new user to the system.
     *
     * @param signUpSecondPhaseRequest the request containing user details for the second phase
     * @param userDetails              the {@link UserDetails} object for the user
     * @return the {@link Users} object if saved successfully
     * @throws EntityNotFoundException if the user type is not found
     */
    public Users addUser(SignUpSecondPhaseRequest signUpSecondPhaseRequest, UserDetails userDetails) {
        Optional<UserType> userType = userTypeRepository.findById(signUpSecondPhaseRequest.getUserType());

        if (userType.isEmpty()) {
            log.error("UserType ID '{}' not found during user addition", signUpSecondPhaseRequest.getUserType());
            throw new EntityNotFoundException("UserType not found during user addition");
        }

        Users user = new Users();
        user.setUserEmail(signUpSecondPhaseRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpSecondPhaseRequest.getPassword()));
        user.setUserType(userType.get());
        user.setUserDetails(userDetails);

        return usersRepository.save(user);
    }

    /**
     * Authenticates a user based on email and password.
     *
     * @param input the login request containing email and password
     * @return an {@link Optional} containing the {@link Users} object if authentication is successful, empty otherwise
     */
    public Optional<Users> authenticate(LoginRequest input) {
        Optional<Users> user = usersRepository.findByUserEmail(input.getEmail());

        if (user.isPresent() && passwordEncoder.matches(input.getPassword(), user.get().getPassword())) {
            log.info("User '{}' authenticated successfully", input.getEmail());
            return user;
        } else {
            log.warn("Authentication failed for '{}'", input.getEmail());
            return Optional.empty();
        }
    }
}
