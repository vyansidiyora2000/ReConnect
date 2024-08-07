package com.dal.asdc.reconnect.dto.Request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Requests {
    String email;
    String name;
    String profile;
    Integer userId;
}
