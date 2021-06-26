import { Injectable } from '@angular/core';
import {NotifierService} from 'angular-notifier';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private notifierService: NotifierService) { }


  public showNotification( type: string, message: string ): void {
    this.notifierService.notify( type, message );
  }

  public showDefaultNotification(message: string ): void {
    this.notifierService.notify( 'default', message );
  }

  public showInfoNotification(message: string ): void {
    this.notifierService.notify( 'info', message );
  }

  public showSuccessNotification(message: string ): void {
    this.notifierService.notify( 'success', message );
  }

  public showWarningNotification(message: string ): void {
    this.notifierService.notify( 'warning', message );
  }

  public showErrorNotification(message: string ): void {
    this.notifierService.notify( 'error', message );
  }
}
