package com.dal.asdc.reconnect.dto.City;

import com.dal.asdc.reconnect.model.Country;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityDTO {
    private int cityId;
    private String cityName;
    private Country country;
}
