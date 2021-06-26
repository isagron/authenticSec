import {Component, Input, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {ModalDismissReasons, NgbActiveModal, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup, NgForm, Validators} from '@angular/forms';
import {UserService} from '../../../service/user.service';
import {User, UserDetails} from '../../../model/user.model';
import {NotificationService} from '../../../service/notification.service';
import {AppError} from '../../../model/error.model';
import {AuthResourcesService} from '../../../service/auth-resources.service';

@Component({
  selector: 'app-new-user-modal',
  templateUrl: './new-user-modal.component.html',
  styleUrls: ['./new-user-modal.component.css']
})
export class NewUserModalComponent implements OnInit {

  public static CREATE_MODE = 'create';
  public static EDIT_MODE = 'edit';

  mode = NewUserModalComponent.CREATE_MODE;

  @Input()
  user: User;

  newUserForm: FormGroup;

  roles: string[] = [];

  roleSelected = 'Select a role';

  error: string;

  buttonLabel = 'Create';


  constructor(private notificationService: NotificationService,
              private userService: UserService,
              private authResourceService: AuthResourcesService,
              public activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
    this.mode = this.user ? NewUserModalComponent.EDIT_MODE : NewUserModalComponent.CREATE_MODE;
    this.buttonLabel = this.mode === NewUserModalComponent.CREATE_MODE ? 'Create' : 'Save';
    const userName = this.mode === NewUserModalComponent.CREATE_MODE ? null : this.user.userName;
    const firstName = this.mode === NewUserModalComponent.CREATE_MODE ? null : this.user.firstName;
    const lastName = this.mode === NewUserModalComponent.CREATE_MODE ? null : this.user.lastName;
    const email = this.mode === NewUserModalComponent.CREATE_MODE ? null : this.user.email;
    const isActive = this.mode === NewUserModalComponent.CREATE_MODE ? null : this.user.isActive;
    const isLock = this.mode === NewUserModalComponent.CREATE_MODE ? null : this.user.isLock;
    this.roleSelected = this.mode === NewUserModalComponent.CREATE_MODE ? null : this.user.role;
    this.newUserForm = new FormGroup({
      userName: new FormControl(userName, Validators.required),
      firstName: new FormControl(firstName, Validators.required),
      lastName: new FormControl(lastName, Validators.required),
      email: new FormControl(email, [Validators.required, Validators.email]),
      isActive: new FormControl(isActive),
      isLock: new FormControl(isLock),
    });

    this.authResourceService.rolesSubject.subscribe(roles => {
      if (roles !== null && roles.length > 0) {
        this.roles = roles.map(role => role.name);
        this.roleSelected = this.user ? this.user.role : this.roles[0];
      }
    });

  }

  saveUser() {
    this.error = null;
    const details = new UserDetails(
      this.newUserForm.value.firstName,
      this.newUserForm.value.lastName,
      this.newUserForm.value.userName,
      this.newUserForm.value.email,
      this.roleSelected,
      this.newUserForm.value.isLock,
      this.newUserForm.value.isActive);
    if (this.mode === NewUserModalComponent.CREATE_MODE) {
      this.userService.createUser(details).subscribe(user => {
          this.notificationService.showSuccessNotification('Create user: ' + user.userName);
          this.activeModal.close();
        },
        (error: AppError) => {
          this.error = 'Failed to create user due to:\n' + error.cause.map(messageCode => messageCode.message).join('\n');
        });
    } else {
      this.userService.reqUpdateUser(this.user.userName, details).subscribe(user => {
          this.notificationService.showSuccessNotification('Edit user: ' + user.userName);
          this.activeModal.close();
        },
        (error: AppError) => {
          this.error = 'Failed to edit user due to:\n' + error.cause.map(messageCode => messageCode.message).join('\n');
        });
    }
  }

  selectRole(roleOption: string) {
    this.roleSelected = roleOption;
  }
}
