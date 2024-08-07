package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserDetails")
@Data
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DetailID")
    private int detailId;

    @Column(name = "UserName", nullable = false, length = 100)
    private String userName;

    @Column(name = "Experience")
    private Integer experience;

    @Column(name = "Resume", columnDefinition = "TEXT")
    private String resume;

    @Column(name = "ProfilePicture", length = 255)
    private String profilePicture;

    @ManyToOne
    @JoinColumn(name = "CityID")
    private City city;

    @ManyToOne
    @JoinColumn(name = "CountryID")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "CurrentCompany")
    private Company company;
}
