import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {Authority, Role} from '../model/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthResourcesService {

  private roles: Role[] = [];
  private authorities: Authority[] = [];

  public rolesSubject = new BehaviorSubject<Role[]>(null);
  public authoritiesSubject = new BehaviorSubject<Authority[]>(null);
  private rolesUrl = environment.apiUrl + '/auth/roles';

  constructor(private http: HttpClient) {
  }

  init() {
    this.reqGetAllRoles();
    this.reqGetAllAuthorities();
  }


  reqCreateAuthority(authority: string) {
    this.http.post(this.rolesUrl + '/authorities', authority).subscribe();
  }

  reqDeleteAuthority(authority: string) {
    this.http.delete(this.rolesUrl + '/authorities/' + authority).subscribe();
  }


  /************************** Role *****************/

  // remote
  reqUpdateRole(selectedRole: Role) {
    this.http.put(this.rolesUrl + '/' + selectedRole.name, selectedRole);
  }

  reqDeleteRole(selectedRole: string) {
    this.http.delete(this.rolesUrl + '/' + selectedRole).subscribe();
  }

  public reqGetAllRoles() {
    return this.http.get<Role[]>(this.rolesUrl)
      .subscribe(roles => {
        if (roles !== null) {
          this.roles = roles;
          this.rolesSubject.next(this.roles.slice());
        }
      });
  }

  reqCreateRole(role: Role) {
    this.http.post(this.rolesUrl, role).subscribe();
  }

  // local

  public addRole(role: Role) {
    this.roles.push(role);
    this.rolesSubject.next(this.roles.slice());
  }

  public deleteRole(deleteRole: Role) {
    this.roles = this.roles.filter(role => role.id !== deleteRole.id);
    this.rolesSubject.next(this.roles.slice());
  }

  public updateRole(updateRole: Role) {
    const roleToUpdate = this.roles.find(role => role.id === updateRole.id);
    roleToUpdate.name = updateRole.name;
    roleToUpdate.authorities = updateRole.authorities;
    this.rolesSubject.next(this.roles.slice());
  }

  /************************** Authorities *****************/

  // remote
  public reqGetAllAuthorities() {
    return this.http.get<Authority[]>(this.rolesUrl + '/authorities')
      .subscribe(authorities => {
        if (!!authorities) {
          this.authorities = authorities;
          this.authoritiesSubject.next(this.authorities.slice());
        }
      });
  }

  // local
  addAuthority(newAuthority: Authority) {
    this.authorities.push(newAuthority);
    this.authoritiesSubject.next(this.authorities.slice());
  }

  deleteAuthority(deleteAuthority: Authority) {
    this.authorities = this.authorities.filter(authority => authority.id !== deleteAuthority.id);
    this.authoritiesSubject.next(this.authorities.slice());
  }

  updateAuthority(updateAuthority: Authority) {
    const authToUpdate = this.authorities.find(authority => authority.id === updateAuthority.id);
    authToUpdate.name = updateAuthority.name;
    authToUpdate.roles = updateAuthority.roles;
    this.authoritiesSubject.next(this.authorities.slice());
  }


}
