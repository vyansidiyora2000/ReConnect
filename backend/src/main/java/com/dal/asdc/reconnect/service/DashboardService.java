package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCompanyDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCountryDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerTypeDTO;
import com.dal.asdc.reconnect.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final DashboardRepository dashboardRepository;

    /**
     * Get all users per country
     *
     * @return List of UsersPerCountryDTO
     */
    public List<UsersPerCountryDTO> getAllUsersPerCountry() {
        log.debug("Retrieving all users per country");
        return dashboardRepository.getAllUsersPerCountry();
    }

    /**
     * Get all users per type
     *
     * @return List of UsersPerTypeDTO
     */
    public List<UsersPerTypeDTO> getAllUsersPerType() {
        log.debug("Retrieving all users per type");
        return dashboardRepository.getAllUsersPerType();
    }

    /**
     * Get all users per company
     *
     * @return List of UsersPerCompanyDTO
     */
    public List<UsersPerCompanyDTO> getAllUsersPerCompany() {
        return dashboardRepository.getAllUsersPerCompany(PageRequest.of(0, Integer.MAX_VALUE));
    }

    /**
     * Get top five companies
     *
     * @return List of UsersPerCompanyDTO
     */
    public List<UsersPerCompanyDTO> getTopFiveCompanies() {
        log.debug("Retrieving top five companies by user count");
        return dashboardRepository.getAllUsersPerCompany(PageRequest.of(0, 5));
    }
}
