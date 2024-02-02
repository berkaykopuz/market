package com.toyota.authorization.entity;

import jakarta.persistence.*;

@Table(name = "roles")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String rolename;

    public Long getId() {
        return id;
    }

    public String getRolename() {
        return rolename;
    }
}
