package com.dal.asdc.reconnect.dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsersPerTypeDTO {
    private Long userCount;
    private int typeId;
    private String typeName;
}
