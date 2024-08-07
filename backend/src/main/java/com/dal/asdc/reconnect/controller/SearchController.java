

package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.dto.Users.SearchResult;
import com.dal.asdc.reconnect.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    /**
     * Retrieves usernames associated with a given company name.
     *
     * @param companyName The name of the company to search for
     * @return ResponseEntity containing a Response object with a list of SearchResult objects if users are found,
     * or an appropriate error response if no users are found or an exception occurs
     */
    @GetMapping("/companies/users")
    public ResponseEntity<?> getUsernamesByCompany(@RequestParam String companyName) {
        try {
            List<SearchResult> users = searchService.findUsernamesByCompanyName(companyName);
            if (users.isEmpty()) {
                Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "No users found for the given company", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                Response<List<SearchResult>> response = new Response<>(HttpStatus.OK.value(), "Users fetched successfully", users);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response<?> response = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing your request", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Retrieves usernames associated with a given username.
     *
     * @param username The name of the username to search for
     * @return ResponseEntity containing a Response object with a list of SearchResult objects if users are found,
     * or an appropriate error response if no users are found or an exception occurs
     */
    @GetMapping("/users")
    public ResponseEntity<?> searchUsernames(@RequestParam(required = false) String username) {

        try {
            List<SearchResult> users = searchService.findAllUsernames(username);
            if (users.isEmpty()) {
                Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "No users found for the given company", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                Response<List<SearchResult>> response = new Response<>(HttpStatus.OK.value(), "Users fetched successfully", users);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response<?> response = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing your request", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}