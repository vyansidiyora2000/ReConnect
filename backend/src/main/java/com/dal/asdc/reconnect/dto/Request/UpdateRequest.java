package com.dal.asdc.reconnect.dto.Request;


import lombok.Data;

@Data
public class UpdateRequest {
    private int userId;
    private boolean status;
}
