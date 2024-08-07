package com.dal.asdc.reconnect.dto.User;

import lombok.Data;

@Data
public class UserDetails {
    String username;
    String email;
    int userId;
    int role;
    String profile;
}
