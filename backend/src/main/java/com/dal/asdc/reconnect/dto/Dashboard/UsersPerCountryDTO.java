package com.dal.asdc.reconnect.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersPerCountryDTO {
    private Long userCount;
    private String countryName;
    private int countryId;
}
