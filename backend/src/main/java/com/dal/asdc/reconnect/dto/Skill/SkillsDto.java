package com.dal.asdc.reconnect.dto.Skill;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillsDto {
    private int skillId;
    private String skillName;
    private int domainId;
    private String domainName;

    public SkillsDto() {

    }
}
