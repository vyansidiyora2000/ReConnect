package com.dal.asdc.reconnect.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersPerCompanyDTO {
    private Long userCount;
    private String companyName;
    private int companyId;
}
