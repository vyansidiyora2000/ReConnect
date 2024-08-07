package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findCitiesByCountryCountryId(int countryId);

    Optional<City> findCityByCityName(String cityName);

    City findCityByCityNameAndCountryCountryId(String cityName, int countryId);

}
