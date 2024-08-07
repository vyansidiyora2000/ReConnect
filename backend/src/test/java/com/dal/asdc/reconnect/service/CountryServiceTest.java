package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Helper.CountryDTO;
import com.dal.asdc.reconnect.model.Country;
import com.dal.asdc.reconnect.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCountryList() {
        Country country1 = new Country();
        country1.setCountryId(1);
        country1.setCountryName("Country1");

        Country country2 = new Country();
        country2.setCountryId(2);
        country2.setCountryName("Country2");

        when(countryRepository.findAll()).thenReturn(Arrays.asList(country1, country2));

        List<CountryDTO> result = countryService.getCountryList();

        assertEquals(2, result.size());
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    void testAddCountry() {
        String countryName = "Country1";
        Country country = new Country();
        country.setCountryName(countryName);

        when(countryRepository.save(any(Country.class))).thenReturn(country);

        Country result = countryService.addCountry(countryName);

        assertEquals(countryName, result.getCountryName());
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    void testModifyCountry() {
        Country existingCountry = new Country();
        existingCountry.setCountryId(1);
        existingCountry.setCountryName("OldCountry");

        CountryDTO countryDTO = new CountryDTO(1, "NewCountry");

        when(countryRepository.findById(1)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(existingCountry);

        Country result = countryService.modifyCountry(countryDTO);

        assertEquals("NewCountry", result.getCountryName());
        verify(countryRepository, times(1)).findById(1);
        verify(countryRepository, times(1)).save(existingCountry);
    }

    @Test
    void testModifyCountry_CountryNotFound() {
        CountryDTO countryDTO = new CountryDTO(1, "NewCountry");

        when(countryRepository.findById(1)).thenReturn(Optional.empty());

        Country result = countryService.modifyCountry(countryDTO);

        assertNull(result);
        verify(countryRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteCountry() {
        Country existingCountry = new Country();
        existingCountry.setCountryId(1);

        when(countryRepository.findById(1)).thenReturn(Optional.of(existingCountry));
        doNothing().when(countryRepository).deleteById(1);

        boolean result = countryService.deleteCountry(1);

        assertTrue(result);
        verify(countryRepository, times(1)).findById(1);
        verify(countryRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteCountry_CountryNotFound() {
        when(countryRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = countryService.deleteCountry(1);

        assertFalse(result);
        verify(countryRepository, times(1)).findById(1);
        verify(countryRepository, times(0)).deleteById(1);
    }

    @Test
    void testGetCountryByName() {
        String countryName = "Country1";
        Country country = new Country();
        country.setCountryName(countryName);

        when(countryRepository.findCountryByCountryName(countryName)).thenReturn(country);

        Country result = countryService.getCountryByName(countryName);

        assertEquals(countryName, result.getCountryName());
        verify(countryRepository, times(1)).findCountryByCountryName(countryName);
    }

    @Test
    void testGetCountryById() {
        Country country = new Country();
        country.setCountryId(1);
        country.setCountryName("Country1");

        when(countryRepository.findById(1)).thenReturn(Optional.of(country));

        Country result = countryService.getCountryById(1);

        assertEquals(1, result.getCountryId());
        assertEquals("Country1", result.getCountryName());
        verify(countryRepository, times(1)).findById(1);
    }

    @Test
    void testGetCountryById_CountryNotFound() {
        when(countryRepository.findById(1)).thenReturn(Optional.empty());

        Country result = countryService.getCountryById(1);

        assertNull(result);
        verify(countryRepository, times(1)).findById(1);
    }
}
