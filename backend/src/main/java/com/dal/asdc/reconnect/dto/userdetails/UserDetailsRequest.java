package com.dal.asdc.reconnect.dto.userdetails;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailsRequest {
    private String userId;
    private String userName;
    private int experience;
    private int company;
    private int city;
    private int country;
    private List<Integer> skillIds;
}
