import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {HeaderComponent} from './components/header/header.component';
import {AppRoutingModule} from './app-routing.module';
import {LoginComponent} from './components/login/login.component';
import {UsersManagementComponent} from './components/users-management/users-management.component';
import {RegisterComponent} from './components/register/register.component';
import {AuthInterceptor} from './service/interceptors/auth.interceptor';
import {AppNotificationModule} from './notification.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { VerifyCodeComponent } from './components/verify-code/verify-code.component';
import { ProfileComponent } from './components/profile/profile.component';
import { NewUserModalComponent } from './components/users-management/new-user-modal/new-user-modal.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RolesComponent } from './components/roles/roles.component';
import { VerificationComponent } from './components/login/insert-verification-code/verification.component';
import {NgWizardConfig, NgWizardModule, THEME} from 'ng-wizard';

const ngWizardConfig: NgWizardConfig = {
  theme: THEME.default
};

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    UsersManagementComponent,
    RegisterComponent,
    VerifyCodeComponent,
    ProfileComponent,
    NewUserModalComponent,
    RolesComponent,
    VerificationComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    AppNotificationModule,
    FormsModule,
    NgbModule,
    ReactiveFormsModule,
    NgWizardModule.forRoot(ngWizardConfig)
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
