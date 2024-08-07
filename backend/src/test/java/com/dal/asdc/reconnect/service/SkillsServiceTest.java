package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Skill.SkillsDto;
import com.dal.asdc.reconnect.model.SkillDomain;
import com.dal.asdc.reconnect.model.Skills;
import com.dal.asdc.reconnect.repository.SkillDomainRepository;
import com.dal.asdc.reconnect.repository.SkillsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SkillsServiceTest {

    @InjectMocks
    private SkillsService skillsService;

    @Mock
    private SkillsRepository skillsRepository;

    @Mock
    private SkillDomainRepository skillDomainRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSkills() {
        SkillDomain domain = new SkillDomain();
        domain.setDomainId(1);
        domain.setDomainName("Domain1");

        Skills skill1 = new Skills();
        skill1.setSkillId(1);
        skill1.setSkillName("Skill1");
        skill1.setSkillDomain(domain);

        Skills skill2 = new Skills();
        skill2.setSkillId(2);
        skill2.setSkillName("Skill2");
        skill2.setSkillDomain(domain);

        when(skillsRepository.findAll()).thenReturn(Arrays.asList(skill1, skill2));

        List<SkillsDto> result = skillsService.getSkills();

        assertEquals(2, result.size());
        verify(skillsRepository, times(1)).findAll();
    }

    @Test
    void testAddSkill() {
        SkillDomain domain = new SkillDomain();
        domain.setDomainId(1);
        domain.setDomainName("Domain1");

        SkillsDto skillDTO = new SkillsDto(1, "Skill1", 1, "Domain1");

        when(skillDomainRepository.findById(1)).thenReturn(Optional.of(domain));

        skillsService.addSkill(skillDTO);

        verify(skillsRepository, times(1)).save(any(Skills.class));
    }

    @Test
    void testAddSkill_SkillDomainNotFound() {
        SkillsDto skillDTO = new SkillsDto(1, "Skill1", 1, "Domain1");

        when(skillDomainRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> skillsService.addSkill(skillDTO));
        assertEquals("Skill Domain not found", exception.getMessage());
    }

    @Test
    void testEditSkill() {
        SkillDomain domain = new SkillDomain();
        domain.setDomainId(1);
        domain.setDomainName("Domain1");

        Skills skill = new Skills();
        skill.setSkillId(1);
        skill.setSkillName("OldSkill");
        skill.setSkillDomain(domain);

        SkillsDto skillDTO = new SkillsDto(1, "NewSkill", 1, "Domain1");

        when(skillsRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillDomainRepository.findById(1)).thenReturn(Optional.of(domain));

        skillsService.editSkill(skillDTO);

        assertEquals("NewSkill", skill.getSkillName());
        verify(skillsRepository, times(1)).save(skill);
    }

    @Test
    void testEditSkill_SkillNotFound() {
        SkillsDto skillDTO = new SkillsDto(1, "NewSkill", 1, "Domain1");

        when(skillsRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> skillsService.editSkill(skillDTO));
        assertEquals("Skill not found", exception.getMessage());
    }

    @Test
    void testEditSkill_SkillDomainNotFound() {
        SkillDomain domain = new SkillDomain();
        domain.setDomainId(1);
        domain.setDomainName("Domain1");

        Skills skill = new Skills();
        skill.setSkillId(1);
        skill.setSkillName("OldSkill");
        skill.setSkillDomain(domain);

        SkillsDto skillDTO = new SkillsDto(1, "NewSkill", 1, "Domain1");

        when(skillsRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillDomainRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> skillsService.editSkill(skillDTO));
        assertEquals("Skill Domain not found", exception.getMessage());
    }

    @Test
    void testDeleteSkill() {
        skillsService.deleteSkill(1);

        verify(skillsRepository, times(1)).deleteById(1);
    }
}
