import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User, UserDetails} from '../model/user.model';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {Page} from '../model/utils.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = environment.apiUrl + '/user';

  public usersPage = new Subject<Page<User>>();

  public user = new BehaviorSubject<User>(null);

  constructor(private http: HttpClient) {
  }

  public createUser(userProp: UserDetails): Observable<User> {
    return this.http.post<User>(this.apiUrl, userProp);
  }

  public reqUpdateUser(userName: string, userProp: UserDetails): Observable<User> {
    return this.http.put<User>(this.apiUrl + '/' + userName, userProp);
  }

  public updateUserImage(userName: string, formData: FormData): Observable<User> {
    // @ts-ignore
    return this.http.put<User>(this.apiUrl + '/' + userName + '/updateProfileImage', formData,
      {
        reportProgress: true,
        observe: 'events'
      });
  }

  public findUser(userName: string): Observable<User> {
    return this.http.get<User>(this.apiUrl + '/' + userName);
  }

  public getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  public getAllUsersPage(userName: string, role: string, pageNumber: number, pageSize: number) {
    let params: HttpParams = new HttpParams();
    if (userName != null) {
      params = params.append('userName', userName);
    }
    if (role != null) {
      params = params.append('role', role);
    }
    params = params.append('page', '' + pageNumber);
    params = params.append('size', '' + pageSize);


    this.http.get<Page<User>>(this.apiUrl + '/page', {
      params
    }).subscribe(
      users => {
        if (users !== null) {
          this.usersPage.next(users);
        }
      }
    );
  }

  public reqDeleteUser(userId: string): Observable<any> {
    return this.http.delete<any>(this.apiUrl + '/' + userId);
  }

  public createImageFormData(profileImage: File): FormData {
    const formData = new FormData();
    formData.append('profileImage', profileImage);
    return formData;
  }

  public init(user: User) {
    this.user.next(user);
  }

  // local
  public notifyOnUserChange(newUser: User) {
    this.getAllUsersPage(null, null, 0, 10);
  }


}
