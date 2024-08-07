package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.dto.Skill.SkillDomainDTO;
import com.dal.asdc.reconnect.service.SkillDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skillDomains")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class SkillDomainController {
    @Autowired
    SkillDomainService skillDomainService;

    /**
     * Retrieves all skill domains from the system.
     *
     * @return ResponseEntity containing a Response object with a list of SkillDomainDTO
     */
    @GetMapping("/getAllSkillDomains")
    public ResponseEntity<?> getAllSkillDomains() {
        List<SkillDomainDTO> listOfSkillDomains = skillDomainService.getAllSkillDomains();
        Response<List<SkillDomainDTO>> response = new Response<>(HttpStatus.OK.value(), "Fetched all skill domains", listOfSkillDomains);
        return ResponseEntity.ok(response);
    }

    /**
     * Adds a new skill domain to the system.
     *
     * @param skillDomainDTO SkillDomainDTO object containing the details of the skill domain to be added
     * @return ResponseEntity containing a Response object with a success message
     */
    @PostMapping("/addSkillDomain")
    public ResponseEntity<?> addSkillDomain(@RequestBody SkillDomainDTO skillDomainDTO) {
        skillDomainService.addSkillDomain(skillDomainDTO);
        return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "Skill domain added successfully", null));
    }

    /**
     * Edits an existing skill domain in the system.
     *
     * @param skillDomainDTO SkillDomainDTO object containing the details of the skill domain to be edited
     * @return ResponseEntity containing a Response object with a success message
     */
    @PutMapping("/editSkillDomain")
    public ResponseEntity<?> editSkillDomain(@RequestBody SkillDomainDTO skillDomainDTO) {
        skillDomainService.editSkillDomain(skillDomainDTO);
        return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "Skill domain updated successfully", null));
    }

    /**
     * Deletes an existing skill domain from the system.
     *
     * @param id Integer value of the skill domain to be deleted
     * @return ResponseEntity containing a Response object with a success message
     */
    @DeleteMapping("/deleteSkillDomain/{id}")
    public ResponseEntity<?> deleteSkillDomain(@PathVariable Integer id) {
        skillDomainService.deleteSkillDomain(id);
        return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "Skill domain deleted successfully", null));
    }
}
