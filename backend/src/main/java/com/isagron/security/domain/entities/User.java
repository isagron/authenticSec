package com.isagron.security.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String userId;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String userName;
    private String password;
    @Column(unique = true)
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;

    @ManyToOne
    private Role role;

    private int successfulLogin;
    private int failureLogin;

    private Date lastTimeRenewPassword;

    private boolean isActive;

    private boolean isLocked;

    private boolean emailVerification;

    private boolean needToReplacePassword;

    public void setRole(Role role){
        if (role != null){
            this.role = role;
            role.addUser(this);
        }
    }

}
