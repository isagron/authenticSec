package com.isagron.security.services.client_notification;

public enum  ClientTopic {

    USER("/topic/user"),
    ROLE("/topic/role"),
    AUTHORITY("/topic/authority");

    public String name;

    ClientTopic(String topic){
        this.name = topic;
    }
}
