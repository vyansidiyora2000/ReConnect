package com.dal.asdc.reconnect.dto.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserNameTypeIdDTO {
    private int userId;
    private String userName;
    private int typeId;
}
