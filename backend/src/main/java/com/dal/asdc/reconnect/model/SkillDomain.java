package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "SkillDomain")
@Data
public class SkillDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DomainID")
    private int domainId;

    @Column(name = "DomainName", nullable = false, length = 100)
    private String domainName;

    @OneToMany(mappedBy = "skillDomain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Skills> skills;
}
