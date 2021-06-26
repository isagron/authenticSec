import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from './service/auth.service';
import {WebSocketService} from './service/web-socket.service';
import {InitService} from './service/init.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'AuthenticSec';

  constructor(private initService: InitService) {
  }

  ngOnInit() {
    this.initService.initializeServices();
  }

  ngOnDestroy() {
    this.initService.destroy();
  }
}
