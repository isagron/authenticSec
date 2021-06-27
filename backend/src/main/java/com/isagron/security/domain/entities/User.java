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

/**
 * Entity class represent a user in the application
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    /**
     * Identifier of the user in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Identifier of the user in the application
     */
    @Column(unique = true)
    private String userId;

    /**
     * User first name
     */
    private String firstName;
    /**
     * User last name
     */
    private String lastName;

    /**
     * User unique user name
     */
    @Column(unique = true)
    private String userName;

    /**
     * User password, saved encrypted
     */
    private String password;

    /**
     * User email, must be unique
     */
    @Column(unique = true)
    private String email;

    /**
     * Image url, url can reference to internal repository or any image url in the internet
     */
    private String profileImageUrl;

    /**
     * Last login date
     */
    private Date lastLoginDate;

    /**
     * Present the user the last login, except the login in the current session
     */
    private Date lastLoginDateDisplay;

    /**
     * The date the user register / created
     */
    private Date joinDate;

    /**
     * The role of the user
     */
    @ManyToOne
    private Role role;

    /**
     * The number of times the user login successfully
     */
    private int successfulLogin;

    /**
     * The number of times user login fails
     */
    private int failureLogin;

    /**
     * The last time the user renew his password
     */
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
