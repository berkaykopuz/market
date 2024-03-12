package com.toyota.token.entity;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
}
