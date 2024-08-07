package com.dal.asdc.reconnect.dto.userdetails;

import com.dal.asdc.reconnect.dto.Skill.SkillsDto;
import lombok.Data;

import java.util.List;

@Data
public class UserDetailsResponse {
    private String userName;
    private int experience;
    private int company;
    private int city;
    private int country;
    private List<SkillsDto> skills;
    private String profilePicture;
}