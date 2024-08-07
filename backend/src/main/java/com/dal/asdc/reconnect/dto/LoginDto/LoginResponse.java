package com.dal.asdc.reconnect.dto.LoginDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private int status;

    private String detail;

    private LoginResponseBody body;

}
