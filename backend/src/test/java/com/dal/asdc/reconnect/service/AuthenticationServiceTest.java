package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.LoginDto.LoginRequest;
import com.dal.asdc.reconnect.dto.SignUp.SignUpFirstPhaseBody;
import com.dal.asdc.reconnect.dto.SignUp.SignUpFirstPhaseRequest;
import com.dal.asdc.reconnect.dto.SignUp.SignUpSecondPhaseRequest;
import com.dal.asdc.reconnect.exception.*;
import com.dal.asdc.reconnect.model.*;
import com.dal.asdc.reconnect.repository.*;
import com.dal.asdc.reconnect.service.AuthenticationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    UserTypeRepository userTypeRepository;

    @Mock
    UsersSkillsRepository usersSkillsRepository;


    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    UsersRepository usersRepository;

    @Mock
    UserDetailsRepository userDetailsRepository;

    @Mock
    CompanyRepository companyRepository;

    @Mock
    CityRepository cityRepository;

    @Mock
    CountryRepository countryRepository;

    @Mock
    SkillsRepository skillsRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    private SignUpSecondPhaseRequest signUpSecondPhaseRequest;

    @Spy
    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateFirstPhase_UserAlreadyPresent() {
        SignUpFirstPhaseRequest request = new SignUpFirstPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setReenteredPassword("Password1!");

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(new Users()));

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.validateFirstPhase(request);
        });

        assertEquals("Email is already present", exception.getMessage());
    }

    @Test
    void testValidateFirstPhase_UserNotPresent() {
        SignUpFirstPhaseRequest request = new SignUpFirstPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setReenteredPassword("Password1!");

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        SignUpFirstPhaseBody response = authenticationService.validateFirstPhase(request);

        assertTrue(response.areAllValuesNull());
    }

    @Test
    void testValidateFirstPhase_ReenterPasswordError() {
        SignUpFirstPhaseRequest request = new SignUpFirstPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setReenteredPassword("DifferentPassword1!");

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        PasswordMismatchException exception = assertThrows(PasswordMismatchException.class, () -> {
            authenticationService.validateFirstPhase(request);
        });

        assertEquals("Passwords do not match", exception.getMessage());
    }

    @Test
    void testValidateFirstPhase_InvalidEmail() {
        SignUpFirstPhaseRequest request = new SignUpFirstPhaseRequest();
        request.setEmail("invalid-email");
        request.setPassword("Password1!");
        request.setReenteredPassword("Password1!");

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> {
            authenticationService.validateFirstPhase(request);
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void testAddNewUser_Success() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1, 2));

        when(userTypeRepository.findById(anyInt())).thenReturn(Optional.of(new UserType()));
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(new City()));
        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(new Country()));
        when(skillsRepository.findById(anyInt())).thenReturn(Optional.of(new Skills()));
        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        doReturn(new UserDetails()).when(authenticationService).addDetails(any(SignUpSecondPhaseRequest.class), anyString());
        doReturn(new Users()).when(authenticationService).addUser(any(SignUpSecondPhaseRequest.class), any(UserDetails.class));
        doReturn(true).when(authenticationService).addSkills(any(SignUpSecondPhaseRequest.class));

        boolean result = authenticationService.addNewUser(request, "");

        assertTrue(result);
    }

    @Test
    @Transactional
    void testAddNewUser_NotSuccess() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setUserType(5);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1, 2));

        when(userTypeRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(new City()));
        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(new Country()));
        when(skillsRepository.findById(anyInt())).thenReturn(Optional.of(new Skills()));
        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(new Users()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        doReturn(new UserDetails()).when(authenticationService).addDetails(any(SignUpSecondPhaseRequest.class), anyString());

        assertFalse(authenticationService.addNewUser(request, ""));
    }

    @Test
    void testAuthenticate_Failure() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword1!");

        Users user = new Users();
        user.setUserEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Optional<Users> result = authenticationService.authenticate(request);

        assertFalse(result.isPresent());
    }

    @Test
    void testAuthenticate_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");

        Users user = new Users();
        user.setUserEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Optional<Users> result = authenticationService.authenticate(request);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.empty());

        Optional<Users> result = authenticationService.authenticate(request);

        assertFalse(result.isPresent());
    }

    @Test
    void testAddDetails_CompanyNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(new Users()));
        when(companyRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(new City()));
        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(new Country()));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.addDetails(request, "file/path");
        });

        assertEquals("Required entity not found during user details addition", exception.getMessage());
    }

    @Test
    void testAddDetails_CityNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(new Users()));
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(new Country()));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.addDetails(request, "file/path");
        });

        assertEquals("Required entity not found during user details addition", exception.getMessage());
    }

    @Test
    void testAddDetails_CountryNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(new Users()));
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(new City()));
        when(countryRepository.findById(anyInt())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.addDetails(request, "file/path");
        });

        assertEquals("Required entity not found during user details addition", exception.getMessage());
    }

    @Test
    void testAddSkills_Success() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setSkills(List.of(1, 2));

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(new Users()));
        when(skillsRepository.findById(anyInt())).thenReturn(Optional.of(new Skills()));

        assertTrue(authenticationService.addSkills(request));
    }

    @Test
    void testAddSkills_Failure() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setSkills(List.of(1, 2));

        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(new Users()));
        when(skillsRepository.findById(anyInt())).thenReturn(Optional.empty());

        SkillNotFoundException exception = assertThrows(SkillNotFoundException.class, () -> {
            authenticationService.addSkills(request);
        });

        assertEquals("Skill not found by ID: 1", exception.getMessage());
    }

    @Test
    void testAddSkills1() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setSkills(List.of(1, 2));
        Users user = new Users();
        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(user));
        when(skillsRepository.findById(1)).thenReturn(Optional.of(new Skills()));
        when(skillsRepository.findById(2)).thenReturn(Optional.of(new Skills()));

        boolean result = authenticationService.addSkills(request);


        assertTrue(result);
    }

    @Test
    void testAddSkills() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setSkills(List.of(1, 2, 3));

        Users user = new Users();
        when(usersRepository.findByUserEmail(anyString())).thenReturn(Optional.of(user));
        when(skillsRepository.findById(1)).thenReturn(Optional.of(new Skills()));
        when(skillsRepository.findById(2)).thenReturn(Optional.of(new Skills()));
        when(skillsRepository.findById(3)).thenReturn(Optional.empty());

        SkillNotFoundException exception = assertThrows(SkillNotFoundException.class, () -> {
            authenticationService.addSkills(request);
        });

        assertEquals("Skill not found by ID: 3", exception.getMessage());
    }


    @Test
    void testAddUser_UserTypeEmpty() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setUserType(1);
        when(userTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            authenticationService.addDetails(request, "file/path");
        });

        assertEquals("Required entity not found during user details addition", exception.getMessage());
    }

    @Test
    void testValidateSecondPhase_AllPresent() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1, 2));

        when(userTypeRepository.findById(anyInt())).thenReturn(Optional.of(new UserType()));
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(anyInt())).thenReturn(Optional.of(new City()));
        when(countryRepository.findById(anyInt())).thenReturn(Optional.of(new Country()));
        when(skillsRepository.findById(anyInt())).thenReturn(Optional.of(new Skills()));


        boolean result = authenticationService.validateSecondPhase(request);


        assertTrue(result);
    }

    @Test
    void testAddNewUser_ValidateSecondPhaseFalse() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        when(userTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

        boolean result = authenticationService.addNewUser(request, "fileName");

        assertFalse(result);
    }

    @Test
    void addSkills_ShouldReturnTrueAndSaveUserSkillsIfAllSkillsExist() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setSkills(Arrays.asList(1, 2, 3));

        Users user = new Users();
        Skills skill1 = new Skills();
        Skills skill2 = new Skills();
        Skills skill3 = new Skills();

        when(usersRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(user));
        when(skillsRepository.findById(1)).thenReturn(Optional.of(skill1));
        when(skillsRepository.findById(2)).thenReturn(Optional.of(skill2));
        when(skillsRepository.findById(3)).thenReturn(Optional.of(skill3));

        boolean result = authenticationService.addSkills(request);

        assertTrue(result, "Expected addSkills to return true if all skills exist");
        verify(usersSkillsRepository, times(3)).save(any(UserSkills.class));
    }

    @Test
    void addUser_ShouldReturnUserIfUserTypeExists() {
        // Create a sample request
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        request.setUserType(1);

        // Create mock objects
        UserDetails userDetails = new UserDetails();
        UserType userType = new UserType();
        userType.setTypeID(1);

        Users expectedUser = new Users();
        expectedUser.setUserEmail(request.getEmail());
        expectedUser.setPassword("encodedPassword");
        expectedUser.setUserType(userType);
        expectedUser.setUserDetails(userDetails);

        // Set up mocks
        when(userTypeRepository.findById(request.getUserType())).thenReturn(Optional.of(userType));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(expectedUser);

        // Call the method
        Users result = authenticationService.addUser(request, userDetails);

        // Verify results
        assertNotNull(result);
        assertEquals(expectedUser.getUserEmail(), result.getUserEmail());
        assertEquals(expectedUser.getPassword(), result.getPassword());
        assertEquals(expectedUser.getUserType(), result.getUserType());
        assertEquals(expectedUser.getUserDetails(), result.getUserDetails());

        // Verify that the mocks were called as expected
        verify(userTypeRepository, times(1)).findById(request.getUserType());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(usersRepository, times(1)).save(any(Users.class));
    }


    @Test
    public void testAddDetails_Success() {
        // Create mock entities with necessary properties
        Company company = new Company(); // Set properties if needed
        City city = new City(); // Set properties if needed
        Country country = new Country(); // Set properties if needed

        // Create request with necessary values
        SignUpSecondPhaseRequest signUpSecondPhaseRequest = new SignUpSecondPhaseRequest();
        signUpSecondPhaseRequest.setCompany(1);
        signUpSecondPhaseRequest.setCity(1);
        signUpSecondPhaseRequest.setCountry(1);
        signUpSecondPhaseRequest.setUserName("TestUser");
        signUpSecondPhaseRequest.setExperience(5);
        signUpSecondPhaseRequest.setResume("resume.pdf");

        String fileNameAndPath = "path/to/profile/picture.jpg";

        // Mock repository behavior
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(countryRepository.findById(1)).thenReturn(Optional.of(country));

        UserDetails mockUserDetails = new UserDetails();
        // Set properties of mockUserDetails if needed

        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(mockUserDetails);

        // Execute the method
        UserDetails result = authenticationService.addDetails(signUpSecondPhaseRequest, fileNameAndPath);

        // Verify results
        assertNotNull(result);
    }


    @Test
    public void testValidateSecondPhase_Success() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1, 2, 3));

        UserType userType = new UserType();
        Company company = new Company();
        City city = new City();
        Country country = new Country();
        Skills skill1 = new Skills();
        Skills skill2 = new Skills();
        Skills skill3 = new Skills();

        when(userTypeRepository.findById(1)).thenReturn(Optional.of(userType));
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(skillsRepository.findById(1)).thenReturn(Optional.of(skill1));
        when(skillsRepository.findById(2)).thenReturn(Optional.of(skill2));
        when(skillsRepository.findById(3)).thenReturn(Optional.of(skill3));

        boolean result = authenticationService.validateSecondPhase(request);

        assertTrue(result);
        verify(userTypeRepository, times(1)).findById(1);
        verify(companyRepository, times(1)).findById(1);
        verify(cityRepository, times(1)).findById(1);
        verify(countryRepository, times(1)).findById(1);
        verify(skillsRepository, times(1)).findById(1);
        verify(skillsRepository, times(1)).findById(2);
        verify(skillsRepository, times(1)).findById(3);
    }

    @Test
    public void testValidateSecondPhase_UserTypeNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1));

        when(userTypeRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                authenticationService.validateSecondPhase(request)
        );

        assertEquals("Required entity not found during validation", exception.getMessage());
    }

    @Test
    public void testValidateSecondPhase_CompanyNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1));

        when(userTypeRepository.findById(1)).thenReturn(Optional.of(new UserType()));
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                authenticationService.validateSecondPhase(request)
        );

        assertEquals("Required entity not found during validation", exception.getMessage());
    }

    @Test
    public void testValidateSecondPhase_CityNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1));

        when(userTypeRepository.findById(1)).thenReturn(Optional.of(new UserType()));
        when(companyRepository.findById(1)).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                authenticationService.validateSecondPhase(request)
        );

        assertEquals("Required entity not found during validation", exception.getMessage());

    }

    @Test
    public void testValidateSecondPhase_CountryNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1));

        when(userTypeRepository.findById(1)).thenReturn(Optional.of(new UserType()));
        when(companyRepository.findById(1)).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(1)).thenReturn(Optional.of(new City()));
        when(countryRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                authenticationService.validateSecondPhase(request)
        );

        assertEquals("Required entity not found during validation", exception.getMessage());
        verify(userTypeRepository, times(1)).findById(1);
        verify(companyRepository, times(1)).findById(1);
        verify(cityRepository, times(1)).findById(1);
        verify(countryRepository, times(1)).findById(1);
        verifyNoInteractions(skillsRepository);
    }

    @Test
    public void testValidateSecondPhase_SkillNotFound() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setUserType(1);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkills(List.of(1, 2, 3));

        when(userTypeRepository.findById(1)).thenReturn(Optional.of(new UserType()));
        when(companyRepository.findById(1)).thenReturn(Optional.of(new Company()));
        when(cityRepository.findById(1)).thenReturn(Optional.of(new City()));
        when(countryRepository.findById(1)).thenReturn(Optional.of(new Country()));
        when(skillsRepository.findById(1)).thenReturn(Optional.of(new Skills()));
        when(skillsRepository.findById(2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SkillNotFoundException.class, () ->
                authenticationService.validateSecondPhase(request)
        );

        assertEquals("Skill ID not found during validation", exception.getMessage());
        verify(userTypeRepository, times(1)).findById(1);
        verify(companyRepository, times(1)).findById(1);
        verify(cityRepository, times(1)).findById(1);
        verify(countryRepository, times(1)).findById(1);
        verify(skillsRepository, times(1)).findById(1);
        verify(skillsRepository, times(1)).findById(2);
    }

    @Test
    void testAddNewUser_AddDetailsReturnsNull() {
        SignUpSecondPhaseRequest request = new SignUpSecondPhaseRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password1!");
        String fileNameAndPath = "file/path";

        when(userDetailsRepository.save(any())).thenReturn(null);

        boolean result = authenticationService.addNewUser(request, fileNameAndPath);

        assertFalse(result);
    }

}
