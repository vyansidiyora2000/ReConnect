package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Company")
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompanyID")
    private int companyId;

    @Column(name = "CompanyName", nullable = false, length = 100)
    private String companyName;
}
