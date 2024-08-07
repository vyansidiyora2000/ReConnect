package com.dal.asdc.reconnect.service;


import com.dal.asdc.reconnect.dto.Users.SearchResult;
import com.dal.asdc.reconnect.model.UserType;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private SearchService searchService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testFindUsernamesByCompanyNameReferent() {
        Users currentUser = new Users();
        currentUser.setUserID(1);
        UserType userType = new UserType();
        userType.setTypeID(1);
        currentUser.setUserType(userType);

        when(authentication.getPrincipal()).thenReturn(currentUser);

        String companyName = "LT";
        List<SearchResult> expectedResults = new ArrayList<>();
        when(usersRepository.findUsersWithDetailsAndReferralStatusWithCompany(1, 2, companyName))
                .thenReturn(expectedResults);

        List<SearchResult> actualResults = searchService.findUsernamesByCompanyName(companyName);

        assertEquals(expectedResults, actualResults);
        verify(usersRepository).findUsersWithDetailsAndReferralStatusWithCompany(1, 2, companyName);
    }

    @Test
    void testFindUsernamesByCompanyNameReferral() {
        Users currentUser = new Users();
        currentUser.setUserID(1);
        UserType userType = new UserType();
        userType.setTypeID(2);
        currentUser.setUserType(userType);

        when(authentication.getPrincipal()).thenReturn(currentUser);

        String companyName = "LT";
        List<SearchResult> expectedResults = new ArrayList<>();
        when(usersRepository.findUsersWithDetailsAndReferentStatusWithCompany(1, 1, companyName))
                .thenReturn(expectedResults);

        List<SearchResult> actualResults = searchService.findUsernamesByCompanyName(companyName);

        assertEquals(expectedResults, actualResults);
        verify(usersRepository).findUsersWithDetailsAndReferentStatusWithCompany(1, 1, companyName);
    }

    @Test
    void testFindAllUsernamesReferent() {
        Users currentUser = new Users();
        currentUser.setUserID(1);
        UserType userType = new UserType();
        userType.setTypeID(1);
        currentUser.setUserType(userType);

        when(authentication.getPrincipal()).thenReturn(currentUser);

        String username = "ABC";
        List<SearchResult> expectedResults = new ArrayList<>();
        when(usersRepository.findUsersWithDetailsAndReferralStatusWithUserName(1, 2, username))
                .thenReturn(expectedResults);

        List<SearchResult> actualResults = searchService.findAllUsernames(username);

        assertEquals(expectedResults, actualResults);
        verify(usersRepository).findUsersWithDetailsAndReferralStatusWithUserName(1, 2, username);
    }

    @Test
    void testFindAllUsernamesReferral() {
        Users currentUser = new Users();
        currentUser.setUserID(1);
        UserType userType = new UserType();
        userType.setTypeID(2);
        currentUser.setUserType(userType);

        when(authentication.getPrincipal()).thenReturn(currentUser);

        String username = "ABC";
        List<SearchResult> expectedResults = new ArrayList<>();
        when(usersRepository.findUsersWithDetailsAndReferentStatusWithUserName(1, 1, username))
                .thenReturn(expectedResults);

        List<SearchResult> actualResults = searchService.findAllUsernames(username);

        assertEquals(expectedResults, actualResults);
        verify(usersRepository).findUsersWithDetailsAndReferentStatusWithUserName(1, 1, username);
    }
}