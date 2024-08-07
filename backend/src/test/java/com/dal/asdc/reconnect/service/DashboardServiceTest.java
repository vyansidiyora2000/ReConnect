package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCompanyDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCountryDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerTypeDTO;
import com.dal.asdc.reconnect.repository.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testGetAllUsersPerCountry() {
        // Prepare mock data
        List<UsersPerCountryDTO> mockUsersPerCountry = new ArrayList<>();
        when(dashboardRepository.getAllUsersPerCountry()).thenReturn(mockUsersPerCountry);

        // Call the service method
        List<UsersPerCountryDTO> result = dashboardService.getAllUsersPerCountry();

        // Verify the results
        assertEquals(mockUsersPerCountry, result);
    }

    @Test
    void testGetAllUsersPerType() {
        // Prepare mock data
        List<UsersPerTypeDTO> mockUsersPerType = new ArrayList<>();
        when(dashboardRepository.getAllUsersPerType()).thenReturn(mockUsersPerType);

        // Call the service method
        List<UsersPerTypeDTO> result = dashboardService.getAllUsersPerType();

        // Verify the results
        assertEquals(mockUsersPerType, result);
    }

    @Test
    void testGetAllUsersPerCompany() {
        // Prepare mock data
        List<UsersPerCompanyDTO> mockUsersPerCompany = new ArrayList<>();
        when(dashboardRepository.getAllUsersPerCompany(PageRequest.of(0, Integer.MAX_VALUE)))
                .thenReturn(mockUsersPerCompany);

        // Call the service method
        List<UsersPerCompanyDTO> result = dashboardService.getAllUsersPerCompany();

        // Verify the results
        assertEquals(mockUsersPerCompany, result);
    }

    @Test
    void testGetTopFiveCompanies() {
        // Prepare mock data
        List<UsersPerCompanyDTO> mockTopFiveCompanies = new ArrayList<>();
        when(dashboardRepository.getAllUsersPerCompany(PageRequest.of(0, 5)))
                .thenReturn(mockTopFiveCompanies);

        // Call the service method
        List<UsersPerCompanyDTO> result = dashboardService.getTopFiveCompanies();

        // Verify the results
        assertEquals(mockTopFiveCompanies, result);
    }
}
