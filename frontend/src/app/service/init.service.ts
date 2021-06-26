import {Injectable} from '@angular/core';
import {AuthResourcesService} from './auth-resources.service';
import {AuthService} from './auth.service';
import {WebSocketService} from './web-socket.service';
import {User} from '../model/user.model';
import {UserService} from './user.service';

@Injectable({
  providedIn: 'root'
})
export class InitService {

  constructor(private authService: AuthService,
              private authResource: AuthResourcesService,
              private userService: UserService,
              private webSocketService: WebSocketService) {
  }

  public initializeServices() {
    if (this.authService.isLoggedIn()) {
      this.startInit(this.authService.getUserFromLocalCache());
    }
    this.authService.authUser.subscribe(user => {
      if (user !== null) {
        this.startInit(user);
      }
    });
  }

  private startInit(user: User) {
    this.authResource.init();
    this.webSocketService.connect();
    this.userService.init(user);
  }

  public destroy() {
    this.webSocketService.disconnect();
  }
}
