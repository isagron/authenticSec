import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {User, UserDetails} from '../../model/user.model';
import {UserService} from '../../service/user.service';
import {Role} from '../../model/auth.model';
import {AuthResourcesService} from '../../service/auth-resources.service';
import {FormControl, FormGroup, NgModel, Validators} from '@angular/forms';
import {Subscription} from 'rxjs';
import {AppError} from '../../model/error.model';
import {NotificationService} from '../../service/notification.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {


  actualUser: User;

  selectedRoleName: string;
  selectedRole: Role = new Role(0, 'none', []);
  authorities: string[] = [];

  roles: Role[] = [];

  imgFile: string;

  successfulLogin = 0;
  failureLogin = 0;

  private userSubscription: Subscription;
  private rolesSubscription: Subscription;
  private generateForm = true;
  userWantToUpdatePhoto = false;

  editUserForm: FormGroup;
  roleForm: FormGroup;
  uploadImageForm: FormGroup;


  constructor(private userService: UserService,
              private authResources: AuthResourcesService,
              private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.userSubscription = this.userService.user.subscribe(user => {
      if (user !== null) {
        this.actualUser = user;
        this.selectedRoleName = user.role;
        if (this.roles !== null && this.roles.length > 0) {
          this.updateSelectedRole(this.selectedRoleName);
        }

        this.successfulLogin =
          this.actualUser.successfulLogin === 0 ? 0
            :
            ((this.actualUser.successfulLogin / (this.actualUser.successfulLogin + this.actualUser.failureLogin)) * 100);

        this.failureLogin =
          this.actualUser.failureLogin === 0 ? 0
            :
            ((this.actualUser.failureLogin / (this.actualUser.failureLogin + this.actualUser.successfulLogin)) * 100);

        if (this.generateForm) {
          this.editUserForm = new FormGroup({
            userName: new FormControl(this.actualUser.userName, Validators.required),
            firstName: new FormControl(this.actualUser.firstName, Validators.required),
            lastName: new FormControl(this.actualUser.lastName, Validators.required),
            email: new FormControl(this.actualUser.email, [Validators.required, Validators.email]),
          });

          this.roleForm = new FormGroup({
            role: new FormControl(this.actualUser.role, Validators.required)
          });

          this.uploadImageForm = new FormGroup({
            name: new FormControl('', [Validators.required]),
            file: new FormControl('', [Validators.required]),
            imgSrc: new FormControl('', [Validators.required])
          });
          this.generateForm = false;
        }
      }
    });

    this.rolesSubscription = this.authResources.rolesSubject.subscribe(roles => {
      if (roles !== null) {
        this.roles = roles;
        if (this.selectedRoleName !== null) {
          this.updateSelectedRole(this.selectedRoleName);
        }
      }
    });


  }

  public roleValueChange($event) {
    this.updateSelectedRole(this.selectedRoleName);
  }

  private updateSelectedRole(roleName: string) {
    this.selectedRole = this.roles.filter((role) => role.name === roleName)[0];
    if (this.selectedRole) {
      this.authorities = this.selectedRole.authorities;
    }
  }

  get uf() {
    return this.uploadImageForm.controls;
  }

  onImageChange(e) {
    const reader = new FileReader();

    if (e.target.files && e.target.files.length) {
      const [file] = e.target.files;
      reader.readAsDataURL(file);

      reader.onload = () => {
        this.imgFile = reader.result as string;
        this.uploadImageForm.patchValue({
          imgSrc: reader.result
        });

      };
    }
  }

  ngOnDestroy() {
    if (!!this.userSubscription) {
      this.userSubscription.unsubscribe();
    }

    if (!!this.rolesSubscription) {
      this.rolesSubscription.unsubscribe();
    }
  }


  saveUserDetails() {
    this.userService.reqUpdateUser(this.actualUser.userName, new UserDetails(
      this.editUserForm.value.firstName,
      this.editUserForm.value.lastName,
      this.editUserForm.value.userName,
      this.editUserForm.value.email,
      this.actualUser.role, this.actualUser.isLock, this.actualUser.isActive
    )).subscribe(user => {
      this.notificationService.showSuccessNotification('Update user: ' + user.userName);
    });
  }

  saveRole() {
    this.userService.reqUpdateUser(this.actualUser.userName, new UserDetails(
      this.actualUser.firstName,
      this.actualUser.lastName,
      this.actualUser.userName,
      this.actualUser.email,
      this.editUserForm.value.role,
      this.actualUser.isLock, this.actualUser.isActive
    )).subscribe(user => {
      this.notificationService.showSuccessNotification('Update user: ' + user.userName);
    });
  }

  uploadImage() {
    const formData = new FormData();
    formData.append('profileImage', this.uploadImageForm.value.file);
    this.userService.updateUserImage(this.actualUser.userName, formData)
      .subscribe(user => {
      this.notificationService.showSuccessNotification('Update image: ' + user.userName);
    });
    this.switchUploadImageMode();
  }

  switchUploadImageMode() {
    this.userWantToUpdatePhoto = !this.userWantToUpdatePhoto;
  }
}
