package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "City")
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CityID")
    private int cityId;

    @Column(name = "CityName", nullable = false, length = 100)
    private String cityName;

    @ManyToOne
    @JoinColumn(name = "CountryID")
    private Country country;

}
