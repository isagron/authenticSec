import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgWizardService, THEME, NgWizardConfig, STEP_STATE, StepChangedArgs, StepValidationArgs} from 'ng-wizard';
import {of, Subscription} from 'rxjs';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../service/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-insert-verification-code',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.css']
})
export class VerificationComponent implements OnInit, OnDestroy {
  userMailForm: FormGroup;
  verificationCodeForm: FormGroup;
  resetPasswordForm: FormGroup;
  resetPasswordFormSubscription: Subscription;

  emailVerified = false;
  errorMessageVerifiedEmail = null;
  emailVerifiedMessage = null;


  codeVerified = false;
  errorMessageConfirmationCode = null;
  confirmationCodeVerifiedMessage = null;

  passwordVerifiedMessage = null;
  passwordErrorMessage = null;

  userName: string = null;
  code: string = null;

  constructor(private ngWizardService: NgWizardService,
              private authService: AuthService,
              private router: Router) {
  }

  stepStates = {
    normal: STEP_STATE.normal,
    disabled: STEP_STATE.disabled,
    error: STEP_STATE.error,
    hidden: STEP_STATE.hidden
  };

  config: NgWizardConfig = {
    selected: 0,
    theme: THEME.circles,
    toolbarSettings: {
      showPreviousButton: false,
      toolbarExtraButtons: [
        {
          text: 'Cancel', class: 'btn btn-warning', event: () => {
            this.cancel();
          }
        },

      ],
    }
  };

  isValidTypeBoolean = true;

  ngOnInit(): void {
    this.userMailForm = new FormGroup({
      email: new FormControl(null, [Validators.required, Validators.email]),
    });
    this.verificationCodeForm = new FormGroup({
      code: new FormControl(null, [Validators.required]),
      userName: new FormControl(null, [Validators.required]),
    });
    this.resetPasswordForm = new FormGroup({
      passwords: new FormGroup({
        password: new FormControl(null, Validators.required),
        confirmPassword: new FormControl(null, Validators.required),
      }, [this.validateConfirmPassword.bind(this)])

    });
  }

  ngOnDestroy() {
    if (!!this.resetPasswordFormSubscription) {
      this.resetPasswordFormSubscription.unsubscribe();
    }
  }

  validateConfirmPassword(control: FormControl): { [key: string]: boolean } {
    if (control.get('password').value !== control.get('confirmPassword').value) {
      console.log('valid');
      return {passwordMustBeMatch: true};
    }
    return null;
  }


  showPreviousStep(event?: Event) {
    this.ngWizardService.previous();
  }

  showNextStep(event?: Event) {
    this.ngWizardService.next();
  }

  resetWizard(event?: Event) {
    this.ngWizardService.reset();
  }

  setTheme(theme: THEME) {
    this.ngWizardService.theme(theme);
  }

  stepChanged(args: StepChangedArgs) {
    console.log(args.step);
    switch (args.step.index) {
      case 0:
        this.startStepInsetMail();
        break;
      case 1:
        this.endStepInsetMail();
        this.startStepInsetCode();
        break;
      case 2:
        this.endStepInsetCode();
        this.startStepResetPassword();
        break;
    }
  }

  private startStepInsetMail() {

  }

  private cancel() {
    this.router.navigate(['/']);
  }

  private endStepInsetMail() {


  }

  private startStepInsetCode() {
  }

  private endStepInsetCode() {

  }

  private startStepResetPassword() {
    this.resetPasswordFormSubscription = this.resetPasswordForm.valueChanges.subscribe(val => {
      if (val.passwords.password === val.passwords.confirmPassword) {
        this.passwordVerifiedMessage = 'Press on Finish to reset the password';
        this.passwordErrorMessage = null;
        this.config.toolbarSettings.toolbarExtraButtons.push({
          text: 'Finish', class: 'btn btn-info', event: () => {
            this.submitNewPassword();
          }
        });
      } else {
        this.passwordVerifiedMessage = null;
        this.passwordErrorMessage = 'Passwords not match';
      }
    });

  }

  private submitNewPassword() {
    const password = this.resetPasswordForm.value.passwords.password;
    const confirmPassword = this.resetPasswordForm.value.passwords.confirmPassword;
    this.authService.requestReset(this.userName, password, confirmPassword, this.code)
      .subscribe(response => {
        this.router.navigate(['/']);
      });
  }

  verifiedEmail() {
    const email = this.userMailForm.value.email;
    this.authService.forgotPassword(email)
      .subscribe(
        response => {
          this.emailVerified = true;
          this.emailVerifiedMessage = 'A message with confirmation code sent to your email, please copy it and move to the next stage';
          this.errorMessageVerifiedEmail = null;
        },
        error => {
          this.errorMessageVerifiedEmail = 'Could not verified email please try again';
          this.emailVerifiedMessage  = null;
        });
  }

  verifiedCode() {
    const code = this.verificationCodeForm.value.code;
    const userName = this.verificationCodeForm.value.userName;
    this.authService.validateCodeForReset(userName, code)
      .subscribe(
        response => {
          this.codeVerified = true;
          this.confirmationCodeVerifiedMessage = 'Your code is valid, please move to the next step to reset your password';
          this.errorMessageConfirmationCode = null;
          this.userName = userName;
          this.code = code;
        },
        error => {
          this.errorMessageConfirmationCode = 'Code or user name incorrect, please copy it from the email you received';
          this.confirmationCodeVerifiedMessage = null;

        });
  }
}
