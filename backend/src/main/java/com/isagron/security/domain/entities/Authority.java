package com.isagron.security.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class represent authority in the server
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authority {
    /**
     * Identifier of the authority
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the authority, must be unique
     */
    @Column(unique = true)
    private String name;

    /**
     * List of role entities associate with this authority
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Role> roles;

    /**
     * Add new role, this method take care of the bidirectional binding
     * @param role - the role to add
     */
    public void addRole(Role role) {
        if (this.roles == null){
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }

    /**
     * Remove the given role
     * @param role - role to remove
     */
    public void removeRole(Role role) {
        this.roles.removeIf(persistRole -> persistRole.getName().equals(role.getName()));
    }
}
