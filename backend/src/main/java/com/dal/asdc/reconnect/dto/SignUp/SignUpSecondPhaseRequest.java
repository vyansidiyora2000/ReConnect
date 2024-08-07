package com.dal.asdc.reconnect.dto.SignUp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignUpSecondPhaseRequest {
    private int userType;
    private String email;
    private String userName;
    private String password;
    private String reenteredPassword;
    private int company;
    private int experience;
    private List<Integer> skills;
    private int country;
    private int city;
    private String resume;
    private MultipartFile profile;
}
