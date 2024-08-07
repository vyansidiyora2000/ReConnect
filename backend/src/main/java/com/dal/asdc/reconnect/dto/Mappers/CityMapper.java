package com.dal.asdc.reconnect.dto.Mappers;

import com.dal.asdc.reconnect.dto.City.CityDTO;
import com.dal.asdc.reconnect.model.City;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CityMapper {
    public CityDTO mapCityToDTO(City city) {
        return new CityDTO(city.getCityId(), city.getCityName(), city.getCountry());
    }

    public City mapDTOToCity(CityDTO cityDTO) {
        City city = new City();
        city.setCityId(cityDTO.getCityId());
        city.setCityName(cityDTO.getCityName());
        city.setCountry(cityDTO.getCountry());
        return city;
    }

    public List<CityDTO> mapCitiesToDTOs(List<City> cities) {
        return cities.stream()
                .map(this::mapCityToDTO)
                .collect(Collectors.toList());
    }
}
