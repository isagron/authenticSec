import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {AppError} from '../../model/error.model';
import {Subscription} from 'rxjs';
import {AuthService} from '../../service/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-verify-code',
  templateUrl: './verify-code.component.html',
  styleUrls: ['./verify-code.component.css']
})
export class VerifyCodeComponent implements OnInit {
  isLoading = false;

  error: string;

  private confirmSubscription: Subscription;

  constructor(private authService: AuthService, private router: Router) {
  }

  ngOnInit(): void {
  }

  onSubmit(form: NgForm) {
    this.isLoading = true;
    this.error = null;
    const email = form.value.userName;
    const token = form.value.confirmToken;
    this.confirmSubscription = this.authService.confirmToken(email, token)
      .subscribe(user => {
          this.isLoading = false;
          this.router.navigate(['/']);
        },
        (error: AppError) => {
          this.isLoading = false;
          this.error = 'Verification failed, due to:\n' + error.cause.map(messageCode => messageCode.message).join('\n');
        });
  }


  sendNewConfirmationCode() {

  }
}
