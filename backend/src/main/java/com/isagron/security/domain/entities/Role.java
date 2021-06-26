package com.isagron.security.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<Authority> authorities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private List<User> users;

    public void addUser(User user) {
        if (this.users == null){
            this.users = new ArrayList<>();
        }
        this.users.add(user);
    }

    public void addAuthorities(List<Authority> authorities) {
        authorities.forEach(this::addAuthority);
    }

    private void addAuthority(Authority authority) {
        if (this.authorities == null){
            this.authorities = new ArrayList<>();
        }
        this.authorities.add(authority);
        authority.addRole(this);
    }

    public void clearAuthorities() {
        if (CollectionUtils.isEmpty(this.authorities)){
            return;
        }
        this.authorities.forEach(authority -> authority.removeRole(this));
        this.authorities.clear();
    }

    public String toString(){
        return this.name;
    }
}
