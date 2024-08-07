package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "UserType")
@Data
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TypeID")
    private int typeID;

    @Column(name = "TypeName", nullable = false)
    private String typeName;
}
