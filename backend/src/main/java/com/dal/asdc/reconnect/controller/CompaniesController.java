package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.Company.CompanyDTO;
import com.dal.asdc.reconnect.dto.Mappers.CompanyMapper;
import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.model.Company;
import com.dal.asdc.reconnect.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class CompaniesController {
    @Autowired
    CompanyService companyService;

    @Autowired
    private CompanyMapper companyMapper;

    @GetMapping("/getAllCompanies")
    public ResponseEntity<?> getAllCompanies() {
        List<CompanyDTO> listOfCompanies = companyService.getAllCompanies();
        Response<List<CompanyDTO>> response = new Response<>(HttpStatus.OK.value(), "Fetched all companies", listOfCompanies);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a company by its ID.
     *
     * @param companyId The ID of the company to retrieve.
     * @return ResponseEntity containing the fetched company information.
     */
    @GetMapping("/getCompany/{companyId}")
    public ResponseEntity<?> getCompanyById(@PathVariable int companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company != null) {
            CompanyDTO companyDTO = companyMapper.mapCompanyToDTO(company);
            Response<CompanyDTO> response = new Response<>(HttpStatus.OK.value(), "Fetched company", companyDTO);
            return ResponseEntity.ok(response);
        } else {
            Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "Company not found!", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Adds a new company.
     *
     * @param companyDTO The CompanyDTO object containing the details of the new company.
     * @return ResponseEntity containing the response for adding the company.
     */
    @PostMapping("/addCompany")
    public ResponseEntity<?> addCompany(@RequestBody CompanyDTO companyDTO) {
        Company existingCompany = companyService.getCompanyById(companyDTO.getCompanyId());
        if (existingCompany != null) {
            Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Company already exists", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            Company newCompany = companyService.addCompany(companyDTO.getCompanyName());
            Map<String, Integer> responseMap = new HashMap<>();
            responseMap.put("companyId", newCompany.getCompanyId());
            Response<Map<String, Integer>> response = new Response<>(HttpStatus.CREATED.value(), "Company saved successfully", responseMap);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    /**
     * Edits an existing company.
     *
     * @param companyDTO The CompanyDTO object containing the updated details of the company.
     * @return ResponseEntity containing the response for editing the company.
     */
    @PutMapping("/editCompany")
    public ResponseEntity<?> editCompany(@RequestBody CompanyDTO companyDTO) {
        Company existingCompany = companyService.getCompanyById(companyDTO.getCompanyId());
        if (existingCompany != null) {
            Company existingCompanyName = companyService.getCompanyByName(companyDTO.getCompanyName());
            if (existingCompanyName != null) {
                Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Company name already exists", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            companyService.modifyCompany(companyDTO);
            Response<CompanyDTO> response = new Response<>(HttpStatus.OK.value(), "Company updated successfully", companyDTO);
            return ResponseEntity.ok(response);
        } else {
            Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "Company does not exist", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Deletes a company by its ID.
     *
     * @param companyId The ID of the company to delete.
     * @return ResponseEntity containing the response for deleting the company.
     */
    @DeleteMapping("/deleteCompany/{companyId}")
    public ResponseEntity<?> deleteCompany(@PathVariable int companyId) {
        Company existingCompany = companyService.getCompanyById(companyId);
        if (existingCompany != null) {
            boolean isCompanyDeleted = companyService.deleteCompany(companyId);
            if (isCompanyDeleted) {
                Response<?> response = new Response<>(HttpStatus.NO_CONTENT.value(), "Company deleted successfully", null);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            } else {
                Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Failed to delete company", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } else {
            Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "Company does not exist", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
