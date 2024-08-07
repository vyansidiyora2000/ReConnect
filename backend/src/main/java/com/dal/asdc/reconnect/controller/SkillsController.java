package com.dal.asdc.reconnect.controller;

import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.dto.Skill.SkillsDto;
import com.dal.asdc.reconnect.service.SkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class SkillsController {

    @Autowired
    SkillsService skillsService;

    /**
     * Retrieves all skills from the system.
     *
     * @return ResponseEntity containing a Response object with a list of SkillsDto
     */
    @GetMapping("/getAllSkills")
    public ResponseEntity<?> getAllSkills() {
        List<SkillsDto> listOfSkills = skillsService.getSkills();
        Response<List<SkillsDto>> response = new Response<>(HttpStatus.OK.value(), "Fetched all skills", listOfSkills);
        return ResponseEntity.ok(response);
    }

    /**
     * Adds a new skill to the system.
     *
     * @param skill SkillsDto object containing the skill details
     * @return ResponseEntity containing a Response object with a success message
     */
    @PostMapping("/addSkill")
    public ResponseEntity<?> addSkill(@RequestBody SkillsDto skill) {
        skillsService.addSkill(skill);
        Response<String> response = new Response<>(HttpStatus.OK.value(), "Skill added successfully", null);
        return ResponseEntity.ok(response);
    }

    /**
     * Edits an existing skill in the system.
     *
     * @param skill SkillsDto object containing the skill details
     * @return ResponseEntity containing a Response object with a success message
     */
    @PutMapping("/editSkill")
    public ResponseEntity<?> editSkill(@RequestBody SkillsDto skill) {
        skillsService.editSkill(skill);
        Response<String> response = new Response<>(HttpStatus.OK.value(), "Skill edited successfully", null);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an existing skill from the system.
     *
     * @param id Integer containing the skill id
     * @return ResponseEntity containing a Response object with a success message
     */
    @DeleteMapping("/deleteSkill/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable("id") Integer id) {
        skillsService.deleteSkill(id);
        Response<String> response = new Response<>(HttpStatus.OK.value(), "Skill deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
