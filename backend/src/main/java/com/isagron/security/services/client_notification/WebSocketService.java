package com.isagron.security.services.client_notification;

import com.isagron.security.domain.dtos.AuthorityDto;
import com.isagron.security.domain.dtos.RoleDto;
import com.isagron.security.domain.dtos.UserDto;
import com.isagron.security.domain.entities.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public <T> void sendMessage(ClientTopic topic, ClientMessageType messageType, T payload){
        ClientMessage<T> message = new ClientMessage<>(messageType, payload);
        this.template.convertAndSend(topic.name, message);
    }

    public void sendUpdateMessage(UserDto user){
        this.sendMessage(ClientTopic.USER, ClientMessageType.UPDATE, user);
    }

    public void sendCreateMessage(UserDto user){
        this.sendMessage(ClientTopic.USER, ClientMessageType.CREATE, user);
    }

    public void sendDeleteMessage(UserDto user){
        this.sendMessage(ClientTopic.USER, ClientMessageType.DELETE, user);
    }

    public void sendUpdateMessage(RoleDto role){
        this.sendMessage(ClientTopic.ROLE, ClientMessageType.UPDATE, role);
    }

    public void sendCreateMessage(RoleDto role){
        this.sendMessage(ClientTopic.ROLE, ClientMessageType.CREATE, role);
    }

    public void sendDeleteMessage(RoleDto role){
        this.sendMessage(ClientTopic.ROLE, ClientMessageType.DELETE, role);
    }

    public void sendUpdateMessage(AuthorityDto authority){
        this.sendMessage(ClientTopic.AUTHORITY, ClientMessageType.UPDATE, authority);
    }

    public void sendCreateMessage(AuthorityDto authority){
        this.sendMessage(ClientTopic.AUTHORITY, ClientMessageType.CREATE, authority);
    }

    public void sendDeleteMessage(AuthorityDto authority){
        this.sendMessage(ClientTopic.AUTHORITY, ClientMessageType.DELETE, authority);
    }
}
