import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {AuthService} from '../auth.service';
import {catchError} from 'rxjs/operators';
import {ErrorService} from '../error.service';
import {NotificationService} from '../notification.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthService,
              private errorService: ErrorService,
              private notificationService: NotificationService) {
  }

  intercept(request: HttpRequest<any>, httpHandler: HttpHandler): Observable<HttpEvent<any>> {
    if (this.isPublicUrl(request.url)) {
      return httpHandler.handle(request).pipe(
        catchError((error: HttpErrorResponse) => {
          return this.handleError(error);
        })
      );
    }
    this.authenticationService.loadToken();
    const token = this.authenticationService.getToken();
    const newRequest = request.clone({setHeaders: {Authorization: `Bearer ${token}`}});
    return httpHandler.handle(newRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        return this.handleError(error);
      })
    );

  }

  private isPublicUrl(url: string) {
    return this.authenticationService.publicUrls.filter(unsecureUrl => url.includes(unsecureUrl)).length > 0;
  }

  private handleError(error: HttpErrorResponse) {
    if (error && error.status === 401) {
      this.authenticationService.logout();
    }
    const appError = this.errorService.mapErrorResponse(error);
    if (appError.message) {
      this.notificationService.showErrorNotification(appError.message);
    } else {
      appError.cause.forEach(codeMessage => {
        this.notificationService.showErrorNotification(codeMessage.message);
      });
    }
    return throwError(appError);
  }
}
