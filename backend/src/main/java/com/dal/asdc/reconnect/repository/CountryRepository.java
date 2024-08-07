package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Country findCountryByCountryName(String countryName);
}
