package com.dal.asdc.reconnect.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dal.asdc.reconnect.dto.userdetails.UserDetailsRequest;
import com.dal.asdc.reconnect.dto.userdetails.UserDetailsResponse;
import com.dal.asdc.reconnect.model.*;
import com.dal.asdc.reconnect.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class ProfileServiceTest {

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UsersSkillsRepository usersSkillsRepository;

    @Mock
    private SkillsRepository skillsRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private Users user;
    private UserDetails userDetails;
    private Company company;
    private City city;
    private Country country;
    private Skills skill;
    private UserSkills userSkill;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new Users();
        user.setUserID(1);

        userDetails = new UserDetails();
        userDetails.setUserName("John Doe");
        userDetails.setExperience(5);

        user.setUserDetails(userDetails);

        skill = new Skills();
        skill.setSkillId(1);
        skill.setSkillName("Java");

        userSkill = new UserSkills();
        userSkill.setUsers(user);
        userSkill.setSkill(skill);

        company = new Company();
        company.setCompanyId(1);

        city = new City();
        city.setCityId(1);

        country = new Country();
        country.setCountryId(1);

        userDetails.setCompany(company);
        userDetails.setCity(city);
        userDetails.setCountry(country);
    }

    @Test
    void testGetUserDetailsByUserID_Success() {
        when(usersRepository.findByUserID(1)).thenReturn(Optional.of(user));
        when(usersSkillsRepository.findByUsersUserID(1)).thenReturn(List.of(userSkill));

        UserDetailsResponse response = profileService.getUserDetailsByUserID(1);

        assertNotNull(response);
        assertEquals("John Doe", response.getUserName());
        assertEquals(5, response.getExperience());
        assertEquals(1, response.getCompany());
        assertEquals(1, response.getCity());
        assertEquals(1, response.getCountry());
        assertEquals(1, response.getSkills().size());
    }

    @Test
    void testGetUserDetailsByUserID_UserNotFound() {
        when(usersRepository.findByUserID(1)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.getUserDetailsByUserID(1));
    }

    @Test
    void testUpdateUserDetails_Success() {
        UserDetailsRequest request = new UserDetailsRequest();
        request.setUserId("1");
        request.setUserName("Jane Doe");
        request.setExperience(10);
        request.setCompany(1);
        request.setCity(1);
        request.setCountry(1);
        request.setSkillIds(Arrays.asList(1));

        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(skillsRepository.findById(1)).thenReturn(Optional.of(skill));
        when(userDetailsRepository.save(any(UserDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDetailsResponse response = profileService.updateUserDetails(request);

        assertNotNull(response);
        assertEquals("Jane Doe", response.getUserName());
        assertEquals(10, response.getExperience());
        assertEquals(1, response.getCompany());
        assertEquals(1, response.getCity());
        assertEquals(1, response.getCountry());
        assertEquals(1, response.getSkills().size());

        verify(usersSkillsRepository, times(1)).deleteByUsers(user);
        verify(usersSkillsRepository, times(1)).saveAll(anyList());
    }


    @Test
    void testUpdateUserDetails_UserNotFound() {
        UserDetailsRequest request = new UserDetailsRequest();
        request.setUserId("1");

        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.updateUserDetails(request));
    }

    @Test
    void testUpdateResumePath_Success() {
        String resumePath = "path/to/resume.pdf";
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        profileService.updateResumePath(1, resumePath);

        assertEquals(resumePath, user.getUserDetails().getResume());
        verify(userDetailsRepository, times(1)).save(userDetails);
    }

    @Test
    void testUpdateResumePath_UserNotFound() {
        String resumePath = "path/to/resume.pdf";
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.updateResumePath(1, resumePath));
    }

    @Test
    void testUpdateProfilePicturePath_Success() {
        String profilePicturePath = "path/to/profile.jpg";
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        profileService.updateProfilePicturePath(1, profilePicturePath);

        assertEquals(profilePicturePath, user.getUserDetails().getProfilePicture());
        verify(userDetailsRepository, times(1)).save(userDetails);
    }

    @Test
    void testUpdateProfilePicturePath_UserNotFound() {
        String profilePicturePath = "path/to/profile.jpg";
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.updateProfilePicturePath(1, profilePicturePath));
    }

    @Test
    void testGetResumePath_Success() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        String resumePath = profileService.getResumePath(1);

        assertEquals(user.getUserDetails().getResume(), resumePath);
    }

    @Test
    void testGetResumePath_UserNotFound() {
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.getResumePath(1));
    }

    @Test
    void testGetProfilePicturePath_Success() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));

        String profilePicturePath = profileService.getProfilePicturePath(1);

        assertEquals(user.getUserDetails().getProfilePicture(), profilePicturePath);
    }

    @Test
    void testGetProfilePicturePath_UserNotFound() {
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> profileService.getProfilePicturePath(1));
    }
}
