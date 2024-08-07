package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Skill.SkillDomainDTO;
import com.dal.asdc.reconnect.model.SkillDomain;
import com.dal.asdc.reconnect.repository.SkillDomainRepository;
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

class SkillDomainServiceTest {

    @InjectMocks
    private SkillDomainService skillDomainService;

    @Mock
    private SkillDomainRepository skillDomainRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSkillDomains() {
        SkillDomain domain1 = new SkillDomain();
        domain1.setDomainId(1);
        domain1.setDomainName("Domain1");

        SkillDomain domain2 = new SkillDomain();
        domain2.setDomainId(2);
        domain2.setDomainName("Domain2");

        when(skillDomainRepository.findAll()).thenReturn(Arrays.asList(domain1, domain2));

        List<SkillDomainDTO> result = skillDomainService.getAllSkillDomains();

        assertEquals(2, result.size());
        verify(skillDomainRepository, times(1)).findAll();
    }

    @Test
    void testAddSkillDomain() {
        SkillDomainDTO skillDomainDTO = new SkillDomainDTO(1, "Domain1");

        skillDomainService.addSkillDomain(skillDomainDTO);

        verify(skillDomainRepository, times(1)).save(any(SkillDomain.class));
    }

    @Test
    void testEditSkillDomain() {
        SkillDomain domain = new SkillDomain();
        domain.setDomainId(1);
        domain.setDomainName("OldDomain");

        SkillDomainDTO skillDomainDTO = new SkillDomainDTO(1, "NewDomain");

        when(skillDomainRepository.findById(1)).thenReturn(Optional.of(domain));

        skillDomainService.editSkillDomain(skillDomainDTO);

        assertEquals("NewDomain", domain.getDomainName());
        verify(skillDomainRepository, times(1)).save(domain);
    }

    @Test
    void testEditSkillDomain_DomainNotFound() {
        SkillDomainDTO skillDomainDTO = new SkillDomainDTO(1, "NewDomain");

        when(skillDomainRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> skillDomainService.editSkillDomain(skillDomainDTO));
        assertEquals("Skill domain not found", exception.getMessage());
    }

    @Test
    void testDeleteSkillDomain() {
        skillDomainService.deleteSkillDomain(1);

        verify(skillDomainRepository, times(1)).deleteById(1);
    }
}
