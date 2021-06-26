import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../service/auth.service';
import {NgForm} from '@angular/forms';
import {AppError} from '../../model/error.model';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  isLoading = false;

  error: string;

  private loginSubscription: Subscription;

  constructor(private router: Router, private authService: AuthService) {
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/users']);
    }
  }


  forgotPassword() {
    this.router.navigate(['/reset-password']);
  }

  onSubmit(form: NgForm) {
    this.isLoading = true;
    this.error = null;
    const email = form.value.userName;
    const password = form.value.password;
    this.loginSubscription = this.authService.login(email, password)
      .subscribe(response => {
          this.isLoading = false;
          this.router.navigate(['/users']);
        },
        (error: AppError) => {
          this.isLoading = false;
          if (this.needToInsertVerificationCode(error)) {
            this.router.navigate(['/verify-code']);
          } else {
            this.error = 'Failed to login due to:\n' + error.cause.map(messageCode => messageCode.message).join('\n');
          }
        });
  }

  needToInsertVerificationCode(error: AppError): boolean {
    if (error.cause.find(messageCode => messageCode.code === 'missing_verification_code')) {
      return true;
    }
    return false;
  }


  ngOnDestroy(): void {
    if (!!this.loginSubscription) {
      this.loginSubscription.unsubscribe();
    }
  }

  requestToResetPassword() {

  }
}
