import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from '../model/user.model';
import {tap} from 'rxjs/operators';
import {JwtHelperService} from '@auth0/angular-jwt';
import {AppError} from '../model/error.model';
import {HeaderType} from '../model/auth.model';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl + '/auth';

  private loginUrl = this.apiUrl + '/login';
  private registerUrl = this.apiUrl + '/register';
  private confirmUrl = this.apiUrl + '/confirm';
  private requestResetPasswordUrl = '/request-reset-password';
  private resetPasswordUrl = '/reset-password';
  private isValidCodeForReset = '/is-valid-code-for-reset';

  public publicUrls = [this.loginUrl, this.registerUrl, this.confirmUrl, this.requestResetPasswordUrl, this.resetPasswordUrl,
    this.isValidCodeForReset];

  private token: string;
  private loggedInUserName: string;
  public authUser = new BehaviorSubject<User>(null);

  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient, private router: Router) {
  }


  public register(userName: string, firstName: string, lastName: string, email: string,
                  password: string, confirmPassword: string): Observable<User | HttpErrorResponse> {
    return this.http.post<User | HttpErrorResponse>(this.registerUrl, {
      userName,
      firstName,
      lastName,
      email,
      password,
      confirmPassword
    });
  }

  public login(userName: string, password: string): Observable<HttpResponse<User> | AppError> {
    return this.http.post<User | AppError>(this.loginUrl, {
      userName,
      password
    }, {observe: 'response'}).pipe(
      tap((response: HttpResponse<User>) => {
        const token = response.headers.get(HeaderType.JWT_TOKEN);
        this.saveToken(token);
        const user = response.body;
        this.saveUser(user);
        this.authUser.next(user);
      })
    );
  }

  public logout(): void {
    this.token = null;
    this.loggedInUserName = null;
    this.authUser.next(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('users');
    this.router.navigate(['/']);
  }

  public confirmToken(userName: string, code: string): Observable<User> {
    return this.http.post<User>(this.apiUrl + this.confirmUrl, {
      userName,
      code
    });
  }

  public validateCodeForReset(userName: string, code: string): Observable<User> {
    return this.http.post<any>(this.apiUrl + this.isValidCodeForReset, {
      userName,
      code
    });
  }

  public forgotPassword(email: string): Observable<any> {
    return this.http.post<User>(this.apiUrl + this.requestResetPasswordUrl,
      {
        email
      });
  }

  public requestReset(userName: string, newPassword: string, confirmPassword: string, confirmation: string): Observable<any> {
    return this.http.post<User>(this.apiUrl + this.resetPasswordUrl, {
      userName,
      newPassword,
      confirmPassword,
      confirmation
    });
  }

  private saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  private saveUser(user: User): void {
    this.loggedInUserName = user.userName;
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user'));
  }

  public loadToken(): void {
    this.token = localStorage.getItem('token');
  }

  public getToken(): string {
    return this.token;
  }

  public isLoginElseLogout(): boolean {
    if (this.isLoggedIn()) {
      return true;
    } else {
      this.logout();
      return false;
    }
  }

  public isLoggedIn(): boolean {
    this.loadToken();
    if (this.token != null && this.token !== '') {
      if (this.jwtHelper.decodeToken(this.token).sub != null || '') {
        if (!this.jwtHelper.isTokenExpired(this.token)) {
          this.loggedInUserName = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        }
      }
    } else {
      return false;
    }
  }

}
