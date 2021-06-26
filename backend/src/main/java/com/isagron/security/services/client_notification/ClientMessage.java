package com.isagron.security.services.client_notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientMessage<T> {

    private ClientMessageType messageType;

    private T payload;
}
