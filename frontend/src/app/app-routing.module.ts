import {AppComponent} from './app.component';
import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {RegisterComponent} from './components/register/register.component';
import {UsersManagementComponent} from './components/users-management/users-management.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './service/guards/auth.guard';
import {VerifyCodeComponent} from './components/verify-code/verify-code.component';
import {ProfileComponent} from './components/profile/profile.component';
import {RolesComponent} from './components/roles/roles.component';
import {VerificationComponent} from './components/login/insert-verification-code/verification.component';

const appRoutes: Routes = [
  {path: '', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'verify-code', component: VerifyCodeComponent},
  {path: 'users', component: UsersManagementComponent, canActivate: [AuthGuard]},
  {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'roles', component: RolesComponent, canActivate: [AuthGuard]},
  {path: 'reset-password', component: VerificationComponent},
];

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes),
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {
}
