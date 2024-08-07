package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.userdetails.UserDetailsRequest;
import com.dal.asdc.reconnect.dto.userdetails.UserDetailsResponse;
import com.dal.asdc.reconnect.factory.UserDetailsResponseFactory;
import com.dal.asdc.reconnect.model.*;
import com.dal.asdc.reconnect.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    public static final String USER_NOT_FOUND_WITH_ID = "User not found with ID: {}";

    private final UserDetailsRepository userDetailsRepository;

    private final UsersRepository usersRepository;

    private final UsersSkillsRepository usersSkillsRepository;

    private final SkillsRepository skillsRepository;

    private final CountryRepository countryRepository;

    private final CityRepository cityRepository;

    private final CompanyRepository companyRepository;

    /**
     * Retrieves user details and skills for a given user ID.
     *
     * @param userID The unique identifier of the user to retrieve details for.
     * @return UserDetailsResponse containing user details and skills.
     * @throws UsernameNotFoundException if no user is found with the given ID.
     */
    public UserDetailsResponse getUserDetailsByUserID(int userID) {
        Optional<Users> userOptional = usersRepository.findByUserID(userID);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            UserDetails userDetails = user.getUserDetails();
            List<UserSkills> skills = usersSkillsRepository.findByUsersUserID(user.getUserID());

            return UserDetailsResponseFactory.create(userDetails, skills);
        } else {
            log.error(USER_NOT_FOUND_WITH_ID, userID);
            throw new UsernameNotFoundException(USER_NOT_FOUND_WITH_ID + userID);
        }
    }

    /**
     * Updates user details and skills based on the provided request.
     * <p>
     * This method performs the following operations within a transaction:
     * 1. Updates the user's basic details (name, experience, company, city, country).
     * 2. Deletes all existing skills for the user.
     * 3. Adds new skills based on the provided skill IDs.
     * 4. Saves all changes to the database.
     *
     * @param request A UserDetailsRequest object containing the updated user information and skill IDs.
     * @return A UserDetailsResponse object containing the updated user details and skills.
     * @throws UsernameNotFoundException if the user is not found.
     * @throws RuntimeException          if the specified company, city, country, or any skill is not found.
     */
    @Transactional
    public UserDetailsResponse updateUserDetails(UserDetailsRequest request) {
        Users user = usersRepository.findById(Integer.valueOf(request.getUserId())).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserDetails userDetails = user.getUserDetails();

        userDetails.setUserName(request.getUserName());
        userDetails.setExperience(request.getExperience());
        Company company = companyRepository.findById(request.getCompany())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        userDetails.setCompany(company);

        City city = cityRepository.findById(request.getCity())
                .orElseThrow(() -> new RuntimeException("City not found"));
        userDetails.setCity(city);

        Country country = countryRepository.findById(request.getCountry())
                .orElseThrow(() -> new RuntimeException("Country not found"));
        userDetails.setCountry(country);

        userDetails = userDetailsRepository.save(userDetails);

        usersSkillsRepository.deleteByUsers(user);

        List<UserSkills> skills = request.getSkillIds().stream().map(skillId -> {
            UserSkills userSkill = new UserSkills();
            userSkill.setUsers(user);
            Skills skill = skillsRepository.findById(skillId)
                    .orElseThrow(() -> new RuntimeException("Skill not found with ID: " + skillId));
            userSkill.setSkill(skill);
            return userSkill;
        }).toList();

        usersSkillsRepository.saveAll(skills);
        return UserDetailsResponseFactory.create(userDetails, skills);
    }

    /**
     * Updates the resume path for a user.
     * <p>
     * This method updates the resume path for the user with the specified ID.
     *
     * @param userId     The ID of the user.
     * @param resumePath The new resume path.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public void updateResumePath(int userId, String resumePath) {
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            UserDetails userDetails = user.get().getUserDetails();
            userDetails.setResume(resumePath);
            userDetailsRepository.save(userDetails);
        } else {
            throw new UsernameNotFoundException(ProfileServiceImpl.USER_NOT_FOUND_WITH_ID + userId);
        }
    }

    /**
     * Updates the profile picture path for a user.
     * <p>
     * This method updates the profile picture path for the user with the specified ID.
     *
     * @param userId             The ID of the user.
     * @param profilePicturePath The new profile picture path.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public void updateProfilePicturePath(int userId, String profilePicturePath) {
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            UserDetails userDetails = user.get().getUserDetails();
            userDetails.setProfilePicture(profilePicturePath);
            userDetailsRepository.save(userDetails);
        } else {
            throw new UsernameNotFoundException(ProfileServiceImpl.USER_NOT_FOUND_WITH_ID + userId);
        }
    }

    /**
     * Gets the resume path for a user.
     * <p>
     * This method retrieves the resume path for the user with the specified ID.
     *
     * @param userId The ID of the user.
     * @return The resume path.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public String getResumePath(int userId) {
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getUserDetails().getResume();
        } else {
            log.error(USER_NOT_FOUND_WITH_ID, userId);
            throw new UsernameNotFoundException(ProfileServiceImpl.USER_NOT_FOUND_WITH_ID + userId);
        }
    }

    /**
     * Gets the profile picture path for a user.
     * <p>
     * This method retrieves the profile picture path for the user with the specified ID.
     *
     * @param userId The ID of the user.
     * @return The profile picture path.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public String getProfilePicturePath(int userId) {
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getUserDetails().getProfilePicture();
        } else {
            log.error(USER_NOT_FOUND_WITH_ID, userId);
            throw new UsernameNotFoundException(ProfileServiceImpl.USER_NOT_FOUND_WITH_ID + userId);
        }
    }
}
