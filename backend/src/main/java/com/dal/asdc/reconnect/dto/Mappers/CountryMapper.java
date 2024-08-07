package com.dal.asdc.reconnect.dto.Mappers;

import com.dal.asdc.reconnect.dto.Helper.CountryDTO;
import com.dal.asdc.reconnect.model.Country;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CountryMapper {

    public CountryDTO mapCountryToDTO(Country country) {
        CountryDTO countryDTO = new CountryDTO(country.getCountryId(), country.getCountryName());
        countryDTO.setCountryId(country.getCountryId());
        countryDTO.setCountryName(country.getCountryName());
        return countryDTO;
    }

    public Country mapDTOToCountry(CountryDTO countryDTO) {
        Country country = new Country();
        country.setCountryId(countryDTO.getCountryId());
        country.setCountryName(countryDTO.getCountryName());
        return country;
    }

    public List<CountryDTO> mapCountriesToDTOs(List<Country> countries) {
        return countries.stream()
                .map(this::mapCountryToDTO)
                .collect(Collectors.toList());
    }
}