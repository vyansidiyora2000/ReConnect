package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.City.CityDTO;
import com.dal.asdc.reconnect.dto.City.CityRequestDTO;
import com.dal.asdc.reconnect.exception.CityNotFoundException;
import com.dal.asdc.reconnect.exception.CountryNotFoundException;
import com.dal.asdc.reconnect.model.City;
import com.dal.asdc.reconnect.model.Country;
import com.dal.asdc.reconnect.repository.CityRepository;
import com.dal.asdc.reconnect.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;

    private final CountryRepository countryRepository;

    /**
     * Retrieves the cities associated with a given country ID.
     *
     * @param country the country for which cities are to be retrieved.
     * @return a list of CityDTO objects containing for the specified country.
     */
    public List<CityDTO> getAllCitiesByCountry(Country country) {
        log.debug("Retrieving all cities for country ID '{}'", country.getCountryId());
        List<CityDTO> listOfCities = new ArrayList<>();
        List<City> listOfCitiesFromDatabase = cityRepository.findCitiesByCountryCountryId(country.getCountryId());
        for (City city : listOfCitiesFromDatabase) {
            CityDTO cityDTO = new CityDTO(city.getCityId(), city.getCityName(), country);
            listOfCities.add(cityDTO);
        }
        log.info("Retrieved {} cities for country ID '{}'", listOfCities.size(), country.getCountryId());
        return listOfCities;
    }

    /**
     * Retrieves a list of CityDTO objects representing all cities.
     *
     * @return List of CityDTO objects containing city information.
     */
    public List<CityDTO> getAllCities() {
        log.debug("Retrieving all cities");
        List<CityDTO> listOfCities = new ArrayList<>();
        List<City> listOfCitiesFromDatabase = cityRepository.findAll();
        for (City city : listOfCitiesFromDatabase) {
            CityDTO cityDTO = new CityDTO(city.getCityId(), city.getCityName(), city.getCountry());
            listOfCities.add(cityDTO);
        }
        log.info("Retrieved {} cities", listOfCities.size());
        return listOfCities;
    }

    /**
     * Retrieves a city by its ID.
     *
     * @param cityId The ID of the city to retrieve.
     * @return City object if found, otherwise null.
     */
    public City getCityById(int cityId) {
        log.debug("Retrieving city with ID '{}'", cityId);
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new CityNotFoundException("City not found with ID: " + cityId));
    }

    /**
     * Retrieves a city by its name and country ID.
     *
     * @param cityName  The name of the city to retrieve.
     * @param countryId The ID of the country the city belongs to.
     * @return City object if found, otherwise null.
     */
    public City getCityByCityNameAndCountryId(String cityName, int countryId) {
        log.debug("Retrieving city with name '{}' and country ID '{}'", cityName, countryId);
        return cityRepository.findCityByCityNameAndCountryCountryId(cityName, countryId);
    }

    /**
     * Adds a new city to the database.
     *
     * @param cityName The name of the city to add.
     * @param country  The Country object representing the country the city belongs to.
     * @return The newly added City object.
     */
    public City addCity(String cityName, Country country) {
        log.debug("Adding new city '{}' for country ID '{}'", cityName, country.getCountryId());
        City newCity = new City();
        newCity.setCityName(cityName);
        newCity.setCountry(country);
        return cityRepository.save(newCity);
    }


    /**
     * Modifies an existing city based on the provided CityRequestDTO.
     *
     * @param cityDTO The CityRequestDTO object containing the updated city information.
     */
    public void modifyCity(CityRequestDTO cityDTO) {
        log.debug("Modifying city with ID '{}'", cityDTO.getCityId());
        City existingCity = getCityById(cityDTO.getCityId());
        existingCity.setCityName(cityDTO.getCityName());

        Country newCountry = countryRepository.findById(cityDTO.getCountryId())
                .orElseThrow(() -> new CountryNotFoundException("Country not found with ID: " + cityDTO.getCountryId()));

        existingCity.setCountry(newCountry);
        cityRepository.save(existingCity);
    }

    /**
     * Deletes a city by its ID.
     *
     * @param cityId The ID of the city to delete.
     * @return True if the city is successfully deleted, false otherwise.
     */
    public boolean deleteCity(int cityId) {
        log.debug("Deleting city with ID '{}'", cityId);
        Optional<City> cityFromDatabase = cityRepository.findById(cityId);
        if (cityFromDatabase.isPresent()) {
            cityRepository.deleteById(cityId);
            return true;
        } else {
            return false;
        }
    }
}
