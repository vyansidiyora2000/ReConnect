package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.Helper.CountryDTO;
import com.dal.asdc.reconnect.dto.Mappers.CountryMapper;
import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.model.Country;
import com.dal.asdc.reconnect.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/countries")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class CountriesController {
    @Autowired
    CountryService countryService;

    @Autowired
    private CountryMapper countryMapper;

    /**
     * Retrieves the list of all countries.
     * Returns a response containing the list of countries along with status and message.
     *
     * @return Response object containing the list of countries.
     */
    @GetMapping("/getAllCountries")
    public ResponseEntity<?> getAllCountries() {
        List<CountryDTO> listOfCountries = countryService.getCountryList();
        Response<List<CountryDTO>> response = new Response<>(HttpStatus.OK.value(), "Fetched all countries", listOfCountries);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a country by its ID.
     *
     * @param countryId The ID of the country to retrieve.
     * @return ResponseEntity containing the fetched country information.
     */
    @GetMapping("/getCountry/{countryId}")
    public ResponseEntity<?> getAllCountries(@PathVariable int countryId) {
        Country country = countryService.getCountryById(countryId);
        if (country != null) {
            CountryDTO countryDTO = countryMapper.mapCountryToDTO(country);
            Response<CountryDTO> response = new Response<>(HttpStatus.OK.value(), "Fetched country", countryDTO);
            return ResponseEntity.ok(response);
        } else {
            Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Country not found!", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * Adds a new country.
     *
     * @param countryDTO The CountryDTO object containing the details of the new country.
     * @return ResponseEntity containing the response for adding the country.
     */
    @PostMapping("/addCountry")
    public ResponseEntity<?> addCountry(@RequestBody CountryDTO countryDTO) {
        Country existingCountry = countryService.getCountryById(countryDTO.getCountryId());
        if (existingCountry != null) {
            Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Country already exists", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            Country newCountry = countryService.addCountry(countryDTO.getCountryName());
            Map<String, Integer> responseMap = new HashMap<>();
            responseMap.put("countryId", newCountry.getCountryId());
            Response<Map<String, Integer>> response = new Response<>(HttpStatus.CREATED.value(), "Country saved successfully", responseMap);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    /**
     * Edits an existing country.
     *
     * @param countryDTO The CountryDTO object containing the updated details of the country.
     * @return ResponseEntity containing the response for editing the country.
     */
    @PutMapping("/editCountry")
    public ResponseEntity<?> editCountry(@RequestBody CountryDTO countryDTO) {
        Country existingCountry = countryService.getCountryById(countryDTO.getCountryId());
        if (existingCountry != null) {
            Country existingCountryName = countryService.getCountryByName(countryDTO.getCountryName());
            if (existingCountryName != null) {
                Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Country name already exists", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            countryService.modifyCountry(countryDTO);
            Response<CountryDTO> response = new Response<>(HttpStatus.OK.value(), "Country updated successfully", countryDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "Country does not exist", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Deletes a country by its ID.
     *
     * @param countryId The ID of the country to delete.
     * @return ResponseEntity containing the response for deleting the country.
     */
    @DeleteMapping("/deleteCountry/{countryId}")
    public ResponseEntity<?> deleteCountry(@PathVariable int countryId) {
        Country existingCountry = countryService.getCountryById(countryId);
        if (existingCountry != null) {
            boolean isCountryDeleted = countryService.deleteCountry(countryId);
            if (isCountryDeleted) {
                Response<?> response = new Response<>(HttpStatus.NO_CONTENT.value(), "Country deleted successfully", null);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            } else {
                Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Failed to delete country", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } else {
            Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "Country does not exist", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
