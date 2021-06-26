import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {Stomp} from '@stomp/stompjs';
import {UserService} from './user.service';
import {AuthResourcesService} from './auth-resources.service';
import {AuthService} from './auth.service';
import * as SockJS from 'sockjs-client';
import {AuthorityMessage, MessageType, RoleMessage, UserMessage} from '../model/notification.model';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private isConnected = false;

  private stompClient = null;

  constructor(private userService: UserService, private authResources: AuthResourcesService, private authService: AuthService) {
    if (!this.isConnected && this.authService.isLoggedIn()) {
      this.connect();
    }

    this.authService.authUser.subscribe(user => {
      if (user !== null && !this.isConnected) {
        this.connect();
      }
    });
  }


  public connect() {
    // build absolute path, websocket doesn't fail during deploying with a context path
    const url = environment.apiUrl + '/security-message-endpoint';
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);
    const headers = {};
    this.stompClient.connect(headers, () => {
      this.isConnected = true;
      this.stompClient.subscribe('/topic/role', msg => {
        if (msg && msg.body) {
          const message: RoleMessage = JSON.parse(msg.body);
          switch (message.messageType) {
            case MessageType.CREATE:
              this.authResources.addRole(message.payload);
              break;
            case MessageType.UPDATE:
              this.authResources.updateRole(message.payload);
              break;
            case MessageType.DELETE:
              this.authResources.deleteRole(message.payload);
              break;
          }
          console.log(message);
        }
      });
      this.stompClient.subscribe('/topic/authority', msg => {
        if (msg && msg.body) {
          const message: AuthorityMessage = JSON.parse(msg.body);
          switch (message.messageType) {
            case MessageType.CREATE:
              this.authResources.addAuthority(message.payload);
              break;
            case MessageType.UPDATE:
              this.authResources.updateAuthority(message.payload);
              break;
            case MessageType.DELETE:
              this.authResources.deleteAuthority(message.payload);
              break;
          }
          console.log(message);
        }

      });
      this.stompClient.subscribe('/topic/user', msg => {
        if (msg && msg.body) {
          const message: UserMessage = JSON.parse(msg.body);
          this.userService.notifyOnUserChange(message.payload);
          console.log(message);
        }

      });

    });
  }

  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
    }

    this.isConnected = false;
    console.log('Disconnected!');
  }
}
