package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Skill.SkillDomainDTO;
import com.dal.asdc.reconnect.model.SkillDomain;
import com.dal.asdc.reconnect.repository.SkillDomainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillDomainService {
    private final SkillDomainRepository skillDomainRepository;

    /**
     * Retrieves the list of all skill domains.
     *
     * @return a list of SkillDomainDTO containing the list of all skill domains.
     */
    public List<SkillDomainDTO> getAllSkillDomains() {
        log.debug("Fetching all skill domains");
        return skillDomainRepository.findAll().stream().map(domain -> new SkillDomainDTO(domain.getDomainId(), domain.getDomainName())).collect(Collectors.toList());
    }

    /**
     * Adds a new skill domain to the database.
     *
     * @param skillDomainDTO The SkillDomainDTO object containing the skill domain information to be added.
     *                       It must include the domain name.
     */
    public void addSkillDomain(SkillDomainDTO skillDomainDTO) {
        log.info("Adding new skill domain: {}", skillDomainDTO);
        SkillDomain skillDomain = new SkillDomain();
        skillDomain.setDomainName(skillDomainDTO.getDomainName());
        skillDomainRepository.save(skillDomain);
    }

    /**
     * Edits an existing skill domain in the database.
     *
     * @param skillDomainDTO The SkillDomainDTO object containing the skill domain information to be edited.
     *                       It must include the domain ID and the domain name.
     * @throws RuntimeException if the specified skill domain is not found.
     */
    public void editSkillDomain(SkillDomainDTO skillDomainDTO) {
        log.info("Adding new skill domain: {}", skillDomainDTO);
        SkillDomain skillDomain = skillDomainRepository.findById(skillDomainDTO.getDomainId()).orElseThrow(() -> new RuntimeException("Skill domain not found"));
        skillDomain.setDomainName(skillDomainDTO.getDomainName());
        skillDomainRepository.save(skillDomain);
    }

    /**
     * Deletes an existing skill domain from the database.
     *
     * @param id The ID of the skill domain to be deleted.
     */
    public void deleteSkillDomain(Integer id) {
        skillDomainRepository.deleteById(id);
    }
}
