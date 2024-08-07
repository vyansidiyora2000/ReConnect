package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.City.CityDTO;
import com.dal.asdc.reconnect.dto.City.CityRequestDTO;
import com.dal.asdc.reconnect.dto.Mappers.CityMapper;
import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.exception.CityNotFoundException;
import com.dal.asdc.reconnect.exception.CountryNotFoundException;
import com.dal.asdc.reconnect.model.City;
import com.dal.asdc.reconnect.model.Country;
import com.dal.asdc.reconnect.service.CityService;
import com.dal.asdc.reconnect.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class CitiesController {
    @Autowired
    CityService cityService;
    @Autowired
    CountryService countryService;
    @Autowired
    private CityMapper cityMapper;

    /**
     * Retrieves all cities or cities by a specific country ID.
     *
     * @param countryId The ID of the country to filter cities by. If null or empty, retrieves all cities.
     * @return ResponseEntity containing a list of CityDTO objects or an error response.
     */
    @GetMapping("/getAllCities")
    public ResponseEntity<?> getAllCitiesByCountryId(@RequestParam(value = "countryId", required = false) String countryId) {
        if (countryId == null || countryId.isEmpty()) {
            List<CityDTO> listOfAllCities = cityService.getAllCities();
            Response<List<CityDTO>> response = new Response<>(HttpStatus.OK.value(), "Fetched all cities", listOfAllCities);
            return ResponseEntity.ok(response);
        } else {
            Country country = countryService.getCountryById(Integer.parseInt(countryId));
            if (country == null) {
                Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Country not found", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else {
                List<CityDTO> listOfAllCities = cityService.getAllCitiesByCountry(country);
                Response<List<CityDTO>> response = new Response<>(HttpStatus.OK.value(), "Fetched all cities", listOfAllCities);
                return ResponseEntity.ok(response);
            }
        }
    }

    /**
     * Retrieves a city by its ID.
     *
     * @param cityId The ID of the city to retrieve.
     * @return ResponseEntity containing the CityDTO object or an error response.
     */
    @GetMapping("/getCity/{cityId}")
    public ResponseEntity<?> getCityByCityId(@PathVariable int cityId) {
        City city = cityService.getCityById(cityId);
        if (city != null) {
            CityDTO cityDTO = cityMapper.mapCityToDTO(city);
            Response<CityDTO> response = new Response<>(HttpStatus.OK.value(), "Fetched City", cityDTO);
            return ResponseEntity.ok(response);
        } else {
            Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "City not found!", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * Adds a new city.
     *
     * @param cityRequestDTO The CityRequestDTO object containing city details.
     * @return ResponseEntity containing the result of the city addition operation.
     */
    @PostMapping("/addCity")
    public ResponseEntity<?> addCity(@RequestBody CityRequestDTO cityRequestDTO) {
        City existingCity = cityService.getCityByCityNameAndCountryId(cityRequestDTO.getCityName(), cityRequestDTO.getCountryId());
        Country country = countryService.getCountryById(cityRequestDTO.getCountryId());
        if (existingCity != null) {
            Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "City already exists in the country", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else if (country == null) {
            Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Country not found", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            City newCity = cityService.addCity(cityRequestDTO.getCityName(), country);
            Map<String, Integer> responseMap = new HashMap<>();
            responseMap.put("cityId", newCity.getCityId());
            Response<Map<String, Integer>> response = new Response<>(HttpStatus.CREATED.value(), "City saved successfully", responseMap);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    /**
     * Update an existing city based on the provided CityDTO.
     * If the city does not exist, return a NOT_FOUND response.
     * If the city name already exists, return a CONFLICT response.
     * If the city is updated successfully, return an OK response.
     *
     * @param cityRequestDTO The CityRequestDTO containing the updated city information.
     * @return ResponseEntity with appropriate response based on the update operation.
     */
    @PutMapping("/editCity")
    public ResponseEntity<?> editCity(@RequestBody CityRequestDTO cityRequestDTO) {
        try {
            cityService.modifyCity(cityRequestDTO);
            Map<String, Integer> responseMap = new HashMap<>();
            responseMap.put("cityId", cityRequestDTO.getCityId());
            Response<Map<String, Integer>> response = new Response<>(HttpStatus.OK.value(), "City updated successfully", responseMap);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (CityNotFoundException | CountryNotFoundException ex) {
            Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception ex) {
            Response<?> response = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Deletes a city by its ID.
     *
     * @param cityId The ID of the city to delete.
     * @return ResponseEntity containing the response for deleting the city.
     */
    @DeleteMapping("/deleteCity/{cityId}")
    public ResponseEntity<?> deleteCity(@PathVariable int cityId) {
        City existingCity = cityService.getCityById(cityId);
        if (existingCity != null) {
            boolean isCityDeleted = cityService.deleteCity(cityId);
            if (isCityDeleted) {
                Response<?> response = new Response<>(HttpStatus.NO_CONTENT.value(), "City deleted successfully", null);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            } else {
                Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Failed to delete city", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } else {
            Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), "City does not exist", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
