package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "UserSkills")
@Entity
@Data
public class UserSkills {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserSkillID")
    private int userSkillId;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "SkillID", nullable = false)
    private Skills skill;
}
