package com.dal.asdc.reconnect.factory;

import com.dal.asdc.reconnect.dto.Skill.SkillsDto;
import com.dal.asdc.reconnect.dto.userdetails.UserDetailsResponse;
import com.dal.asdc.reconnect.model.UserDetails;
import com.dal.asdc.reconnect.model.UserSkills;

import java.util.List;

public class UserDetailsResponseFactory {
    private UserDetailsResponseFactory() {
    }

    public static UserDetailsResponse create(UserDetails userDetails, List<UserSkills> userSkills) {
        UserDetailsResponse response = new UserDetailsResponse();
        response.setUserName(userDetails.getUserName());
        response.setExperience(userDetails.getExperience());
        response.setCompany(userDetails.getCompany().getCompanyId());
        response.setCity(userDetails.getCity().getCityId());
        response.setCountry(userDetails.getCountry().getCountryId());
        response.setProfilePicture(userDetails.getProfilePicture());

        List<SkillsDto> skills = userSkills.stream().map(skill -> {
            SkillsDto dto = new SkillsDto();
            dto.setSkillId(skill.getSkill().getSkillId());
            dto.setSkillName(skill.getSkill().getSkillName());
            return dto;
        }).toList();

        response.setSkills(skills);
        return response;
    }
}
