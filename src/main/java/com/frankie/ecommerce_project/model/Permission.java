package com.frankie.ecommerce_project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {

    private String name;

    private String path;

    private String method;

    private String module;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
}
