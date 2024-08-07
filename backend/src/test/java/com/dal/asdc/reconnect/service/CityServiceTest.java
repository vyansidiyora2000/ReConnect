package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.City.CityDTO;
import com.dal.asdc.reconnect.dto.City.CityRequestDTO;
import com.dal.asdc.reconnect.exception.CityNotFoundException;
import com.dal.asdc.reconnect.exception.CountryNotFoundException;
import com.dal.asdc.reconnect.model.City;
import com.dal.asdc.reconnect.model.Country;
import com.dal.asdc.reconnect.repository.CityRepository;
import com.dal.asdc.reconnect.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CityServiceTest {

    @Mock
    CityRepository cityRepository;

    @Mock
    CountryRepository countryRepository;

    @InjectMocks
    CityService cityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCityById() {
        // Mock data
        Country country = new Country();
        country.setCountryName("USA");
        country.setCountryId(1);
        City city = new City();
        city.setCityName("New York");
        city.setCityId(1);
        city.setCountry(country);
        Optional<City> optionalCity = Optional.of(city);

        // Mocking the behavior of cityRepository.findById(cityId)
        when(cityRepository.findById(1)).thenReturn(optionalCity);

        // Calling the method to be tested
        City result = cityService.getCityById(1);

        // Asserting the result
        assertEquals(city, result);
    }

    @Test
    void testCityNotFoundException() {
        when(cityRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> {
            cityService.getCityById(2);
        });
    }

    @Test
    void testGetCityByCityNameAndCountryId() {
        Country country = new Country();
        country.setCountryId(1);
        City city = new City();
        city.setCityName("New York");
        city.setCityId(1);
        city.setCountry(country);
        when(cityRepository.findCityByCityNameAndCountryCountryId("New York", 1)).thenReturn(city);

        City result = cityService.getCityByCityNameAndCountryId("New York", 1);
        assertEquals(city, result);
    }

    @Test
    void testAddCity() {
        Country country = new Country();
        country.setCountryId(1);
        City city = new City();
        city.setCityName("New City");
        city.setCountry(country);

        when(cityRepository.save(any(City.class))).thenReturn(city);

        City result = cityService.addCity("New City", country);
        assertEquals(city, result);
    }

    @Test
    void testModifyCity() {
        Country oldCountry = new Country();
        oldCountry.setCountryId(1);
        oldCountry.setCountryName("USA");

        Country newCountry = new Country();
        newCountry.setCountryId(2);
        newCountry.setCountryName("Canada");

        City oldCity = new City();
        oldCity.setCityId(1);
        oldCity.setCityName("Old City");
        oldCity.setCountry(oldCountry);

        City updatedCity = new City();
        updatedCity.setCityId(1);
        updatedCity.setCityName("Updated City");
        updatedCity.setCountry(newCountry);

        CityRequestDTO cityRequestDTO = new CityRequestDTO();
        cityRequestDTO.setCityId(1);
        cityRequestDTO.setCityName("Updated City");
        cityRequestDTO.setCountryId(2);

        when(cityRepository.findById(1)).thenReturn(Optional.of(oldCity));
        when(countryRepository.findById(2)).thenReturn(Optional.of(newCountry));
        when(cityRepository.save(any(City.class))).thenReturn(updatedCity);

        cityService.modifyCity(cityRequestDTO);
        assertEquals("Updated City", oldCity.getCityName());
        assertEquals(newCountry, oldCity.getCountry());
    }

    @Test
    void testDeleteCity() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(new City()));
        doNothing().when(cityRepository).deleteById(1);

        boolean result = cityService.deleteCity(1);
        assertTrue(result);
    }

    @Test
    void testDeleteCityNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = cityService.deleteCity(1);
        assertFalse(result);
    }

    @Test
    void testGetAllCitiesByCountry() {
        Country country = new Country();
        country.setCountryId(1);
        country.setCountryName("USA");

        City city1 = new City();
        city1.setCityId(1);
        city1.setCityName("New York");
        city1.setCountry(country);

        City city2 = new City();
        city2.setCityId(2);
        city2.setCityName("Los Angeles");
        city2.setCountry(country);

        List<City> cities = List.of(city1, city2);

        when(cityRepository.findCitiesByCountryCountryId(1)).thenReturn(cities);

        List<CityDTO> result = cityService.getAllCitiesByCountry(country);

        assertEquals(2, result.size());
        assertEquals("New York", result.get(0).getCityName());
        assertEquals("Los Angeles", result.get(1).getCityName());
    }

    @Test
    void testGetAllCities() {
        Country country = new Country();
        country.setCountryId(1);
        country.setCountryName("USA");

        City city1 = new City();
        city1.setCityId(1);
        city1.setCityName("New York");
        city1.setCountry(country);

        City city2 = new City();
        city2.setCityId(2);
        city2.setCityName("Los Angeles");
        city2.setCountry(country);

        List<City> cities = List.of(city1, city2);

        when(cityRepository.findAll()).thenReturn(cities);

        List<CityDTO> result = cityService.getAllCities();

        assertEquals(2, result.size());
        assertEquals("New York", result.get(0).getCityName());
        assertEquals("Los Angeles", result.get(1).getCityName());
    }
}
