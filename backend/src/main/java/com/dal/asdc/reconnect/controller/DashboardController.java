package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCompanyDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerCountryDTO;
import com.dal.asdc.reconnect.dto.Dashboard.UsersPerTypeDTO;
import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.service.DashboardService;
import com.dal.asdc.reconnect.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    JWTService jwtService;

    @Autowired
    DashboardService dashboardService;

    /**
     * This method is used to get all the users per country
     *
     * @return ResponseEntity<Response < List < UsersPerCountryDTO>>>
     */
    @GetMapping("/getAllUsersPerCountry")
    public ResponseEntity<Response<List<UsersPerCountryDTO>>> getAllUsersPerCountry() {
        List<UsersPerCountryDTO> usersPerCountryDTOS = dashboardService.getAllUsersPerCountry();
        Response<List<UsersPerCountryDTO>> usersPerCountryDTOResponse = new Response<>(HttpStatus.OK.value(), "Fetched Users", usersPerCountryDTOS);
        return ResponseEntity.ok(usersPerCountryDTOResponse);
    }

    /**
     * This method is used to get all the users per type
     *
     * @return ResponseEntity<Response < List < UsersPerTypeDTO>>>
     */
    @GetMapping("/getAllUsersPerType")
    public ResponseEntity<Response<List<UsersPerTypeDTO>>> getAllUsersPerType() {
        List<UsersPerTypeDTO> usersPerCountryDTOS = dashboardService.getAllUsersPerType();
        Response<List<UsersPerTypeDTO>> usersPerTypeDTOResponse = new Response<>(HttpStatus.OK.value(), "Fetched Users", usersPerCountryDTOS);
        return ResponseEntity.ok(usersPerTypeDTOResponse);
    }

    /**
     * This method is used to get all the users per company
     *
     * @return ResponseEntity<Response < List < UsersPerCompanyDTO>>>
     */
    @GetMapping("/getAllUsersPerCompany")
    public ResponseEntity<Response<List<UsersPerCompanyDTO>>> getAllUsersPerSkill() {
        List<UsersPerCompanyDTO> usersPerCountryDTOS = dashboardService.getAllUsersPerCompany();
        Response<List<UsersPerCompanyDTO>> usersPerCompanyDTOResponse = new Response<>(HttpStatus.OK.value(), "Fetched Users", usersPerCountryDTOS);
        return ResponseEntity.ok(usersPerCompanyDTOResponse);
    }

    /**
     * This method is used to get top five countries
     *
     * @return ResponseEntity<Response < List < UsersPerCountryDTO>>>
     */
    @GetMapping("/getTopFiveCompanies")
    public ResponseEntity<Response<List<UsersPerCompanyDTO>>> getTopFiveCompanies() {
        List<UsersPerCompanyDTO> usersPerCountryDTOS = dashboardService.getTopFiveCompanies();
        Response<List<UsersPerCompanyDTO>> usersPerCompanyDTOResponse = new Response<>(HttpStatus.OK.value(), "Fetched Users", usersPerCountryDTOS);
        return ResponseEntity.ok(usersPerCompanyDTOResponse);
    }

}
