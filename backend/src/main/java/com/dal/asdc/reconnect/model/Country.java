package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Country")
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CountryID")
    private int countryId;

    @Column(name = "CountryName", nullable = false, length = 100)
    private String countryName;
}
