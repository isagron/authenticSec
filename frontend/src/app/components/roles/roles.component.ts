import {Component, OnInit} from '@angular/core';
import {Role} from '../../model/auth.model';
import {AuthResourcesService} from '../../service/auth-resources.service';
import {Subscription} from 'rxjs';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-roles',
  templateUrl: './roles.component.html',
  styleUrls: ['./roles.component.css']
})
export class RolesComponent implements OnInit {
  newRoleForm: FormGroup;
  userWantToAddRole = true;
  createNewRoleShow = true;
  authoritiesForNewRole = [];

  roles: Role[] = [
    new Role(1, 'User admin', [
      'user:read',
      'user:write']),
    new Role(2, 'Super admin', [
      'user:read',
      'user:write']),
    new Role(3, 'Super2 admin', [
      'user:read',
      'user:write']),
  ];

  authorities: string[] = ['user:read', 'user:write'];

  private authoritiesSub: Subscription;
  private rolesSub: Subscription;


  constructor(private authResource: AuthResourcesService) {

  }

  ngOnInit(): void {
    this.authoritiesSub = this.authResource.authoritiesSubject.subscribe(authorities => {
      if (authorities !== null) {
        this.authorities = authorities.map(auth => auth.name);
      }
    });

    this.rolesSub = this.authResource.rolesSubject.subscribe(roles => {
      if (roles !== null) {
        this.roles = roles;
      }
    });

    this.newRoleForm = new FormGroup({
      roleName: new FormControl(null, Validators.required)
    });
  }


  saveRole(selectedRole: Role) {
    this.authResource.reqUpdateRole(selectedRole);
  }

  deleteRole(selectedRole: string) {
    this.authResource.reqDeleteRole(selectedRole);
  }

  isRoleAsAuth(role: Role, authority: string): boolean {
    return role.authorities.indexOf(authority) >= 0;
  }

  selectAuthority(selectedAuthority: string, selectedRole: Role) {
    const role = this.roles[this.roles.indexOf(selectedRole)];
    if (this.isRoleAsAuth(role, selectedAuthority)) {
      role.authorities = role.authorities.filter(auth => auth !== selectedAuthority);
    } else {
      role.authorities.push(selectedAuthority);
    }
  }

  openRoleForm() {
    this.userWantToAddRole = !this.userWantToAddRole;
    this.createNewRoleShow = false;
  }

  createRole() {
    const name = this.newRoleForm.value.roleName;
    const role = new Role(null, name, this.authoritiesForNewRole);
    this.authResource.reqCreateRole(role);
    this.cancelNewRole();
  }

  selectAuthorityForNewRole(authority: string) {
    if (this.authoritiesForNewRole.indexOf(authority) > 0) {
      this.authoritiesForNewRole = this.authoritiesForNewRole.filter(a => a !== authority);
    } else {
      this.authoritiesForNewRole.push(authority);
    }
  }

  cancelNewRole() {
    this.newRoleForm.reset();
    this.authoritiesForNewRole = [];
    this.createNewRoleShow = true;
  }

  addAuthority(newAuthority: HTMLInputElement) {
    const authority = newAuthority.value;
    if (authority && authority !== '') {
      this.authResource.reqCreateAuthority(authority);
    }
    newAuthority.value = '';
  }

  deleteAuthority(auth: string) {
    this.authResource.reqDeleteAuthority(auth);
  }


}
