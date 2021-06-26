import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../service/auth.service';
import {UserService} from '../../service/user.service';
import {User} from '../../model/user.model';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  isAuthenticated = false;

  isInLoginPage = true;

  user: User;

  private userSubscription: Subscription;

  constructor(private authService: AuthService, private userService: UserService) {
    this.isAuthenticated = authService.isLoggedIn();
    if (this.isAuthenticated) {
      this.user = this.authService.getUserFromLocalCache();
      this.isInLoginPage = false;
    }

    this.userSubscription = this.authService.authUser.subscribe(user => {
      if (user !== null) {
        this.isAuthenticated = true;
        this.user = user;
        this.isInLoginPage = false;
      }
    });

  }

  ngOnInit(): void {
  }

  onLogout() {
    this.authService.logout();
    this.isAuthenticated = !this.isAuthenticated;
  }

  switchMode() {
    this.isInLoginPage = !this.isInLoginPage;
  }

  ngOnDestroy(): void {
    if (!!this.userSubscription){
      this.userSubscription.unsubscribe();
    }
  }
}
