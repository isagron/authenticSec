package com.isagron.security.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Entity class to manage confirmation code,
 * Code confirmation is asynchronous operation, cause the code sends to the user email.
 * We need to persist the procedure state
 */
@Entity
@Data
@NoArgsConstructor
public class ConfirmationToken {

    /**
     * Code confirmation identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The code
     */
    private String confirmationToken;

    /**
     * When the code was created, used to verified if it expire
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    /**
     * Code associate to a single user
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    public ConfirmationToken(User user){
        this.confirmationToken = UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.user = user;
    }

    public boolean isExpire(int confirmationCodeExpirationInSec) {
        Calendar inst = Calendar.getInstance();
        inst.setTime(createdDate);
        inst.add(Calendar.SECOND, confirmationCodeExpirationInSec);
        return new Date().compareTo(inst.getTime()) > 0;
    }
}
