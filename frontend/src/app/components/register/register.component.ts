import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, NgForm, Validators} from '@angular/forms';
import {AppError} from '../../model/error.model';
import {Router} from '@angular/router';
import {AuthService} from '../../service/auth.service';
import {Subscription} from 'rxjs';
import {User} from '../../model/user.model';
import {NotificationService} from '../../service/notification.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  isLoading = false;

  error: string;

  private registerSubscription: Subscription;

  registerForm: FormGroup;

  constructor(private router: Router, private authService: AuthService, private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.registerForm = new FormGroup({
      userName: new FormControl(null, Validators.required),
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
      email: new FormControl(null, [Validators.required, Validators.email]),
      passwords: new FormGroup({
        password: new FormControl(null, Validators.required),
        confirmPassword: new FormControl(null, Validators.required),
      }, [this.validateConfirmPassword.bind(this)])

    });
  }

  validateConfirmPassword(control: FormControl): { [key: string]: boolean } {
    if (control.get('password').value !== control.get('confirmPassword').value) {
      console.log('valid');
      return {'passwordMustBeMatch': true};
    }
    return null;
  }

  onSubmit() {
    this.isLoading = true;
    this.error = null;
    const userName = this.registerForm.value.userName;
    const email = this.registerForm.value.email;
    const password = this.registerForm.value.password;
    const confirmPassword = this.registerForm.value.confirmPassword;
    const firstName = this.registerForm.value.firstName;
    const lastName = this.registerForm.value.lastName;

    if (password !== confirmPassword) {
      this.error = 'Passwords not match';
      return;
    }

    this.registerSubscription = this.authService.register(userName, firstName, lastName, email, password, confirmPassword)
      .subscribe((user: User) => {
          this.isLoading = false;
          this.notificationService.showSuccessNotification('Register successfully');
          if (user.emailVerification) {
            this.router.navigate(['/verify - code']);
          } else {
            this.router.navigate(['/']);
          }
        },
        (error: AppError) => {
          this.isLoading = false;
          this.error = 'Failed to login due to:\n' + error.cause.map(messageCode => messageCode.message).join('\n');
        });
  }

}
