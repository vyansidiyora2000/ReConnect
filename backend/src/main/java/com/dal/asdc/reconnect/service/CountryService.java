package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Helper.CountryDTO;
import com.dal.asdc.reconnect.model.Country;
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
public class CountryService {

    private final CountryRepository countryRepository;

    /**
     * Retrieves the list of all countries.
     *
     * @return a CountryResponseBody object containing the list of all countries.
     */
    public List<CountryDTO> getCountryList() {
        log.debug("Retrieving all countries");
        List<CountryDTO> listOfCountries = new ArrayList<>();
        List<Country> listOfCountriesFromDatabase = countryRepository.findAll();
        for (Country country : listOfCountriesFromDatabase) {
            CountryDTO countryDTO = new CountryDTO(country.getCountryId(), country.getCountryName());
            listOfCountries.add(countryDTO);
        }
        return listOfCountries;
    }

    /**
     * Adds a new country with the given country name.
     *
     * @param countryName The name of the country to add.
     * @return The newly added Country object.
     */
    public Country addCountry(String countryName) {
        log.debug("Adding new country with name '{}'", countryName);
        Country country = new Country();
        country.setCountryName(countryName);
        countryRepository.save(country);
        return country;
    }

    /**
     * Modifies an existing country with the details from the provided CountryDTO.
     *
     * @param country The CountryDTO object containing the updated details of the country.
     * @return The modified Country object.
     */
    public Country modifyCountry(CountryDTO country) {
        log.debug("Modifying country with ID '{}'", country.getCountryId());
        Optional<Country> countryFromDatabase = countryRepository.findById(country.getCountryId());
        if (countryFromDatabase.isPresent()) {
            Country existingCountry = countryFromDatabase.get();
            existingCountry.setCountryName(country.getCountryName());
            countryRepository.save(existingCountry);
            return existingCountry;
        } else {
            return null;
        }
    }

    /**
     * Deletes a country by its ID.
     *
     * @param countryId The ID of the country to delete.
     * @return True if the country is successfully deleted, false otherwise.
     */
    public boolean deleteCountry(int countryId) {
        log.debug("Deleting country with ID '{}'", countryId);
        Optional<Country> countryFromDatabase = countryRepository.findById(countryId);
        if (countryFromDatabase.isPresent()) {
            countryRepository.deleteById(countryId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves a country by its name.
     *
     * @param countryName The name of the country to retrieve.
     * @return The Country object with the specified name.
     */
    public Country getCountryByName(String countryName) {
        log.debug("Retrieving country with name '{}'", countryName);
        return countryRepository.findCountryByCountryName(countryName);
    }

    /**
     * Retrieves a country by its ID.
     *
     * @param countryId The ID of the country to retrieve.
     * @return The Country object with the specified ID, or null if not found.
     */
    public Country getCountryById(int countryId) {
        log.debug("Retrieving country with ID '{}'", countryId);
        Optional<Country> country = countryRepository.findById(countryId);
        return country.orElse(null);
    }
}
