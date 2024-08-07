package com.dal.asdc.reconnect.dto.ResetPassword;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}