import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { UserRegistrationPg1Component } from './user-registration-pg1/user-registration-pg1.component';
import { UserRegistrationPg3Component } from './user-registration-pg3/user-registration-pg3.component';
import { UserRegistrationPg2Component } from './user-registration-pg2/user-registration-pg2.component';
import { UserRegistrationPg4Component } from './user-registration-pg4/user-registration-pg4.component';
import { HomepageComponent } from './homepage/homepage.component';
import {UserRegistrationComponent} from './user-registration/user-registration.component';
import {AuthGuard} from './gaurds/auth.gaurd';
import {BankHomeComponent} from './bank-home/bank-home.component';
import {TransactionComponent} from './transaction/transaction.component';
import {SummaryComponent} from './summary/summary.component';
import {ProfilePageComponent} from './profile-page/profile-page.component';
import { TransferFormComponent } from './transfer-form/transfer-form.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ForgotPasswordPg2Component } from './forgot-password-pg2-verify-securitycode/forgot-password-pg2.component';
import { TransferHomepageComponent } from './transfer-homepage/transfer-homepage.component';
import { PayeeComponent } from './payee/payee.component';


const routes: Routes = [
  { path: '', redirectTo: 'pilotbank', pathMatch: 'full' },
  { path: 'pilotbank', component: BankHomeComponent},
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomepageComponent, canActivate : [AuthGuard] },
  { path: 'myprofile', component: ProfilePageComponent },
  { path: 'transaction', component: TransactionComponent },
  { path: 'summary', component: SummaryComponent },
  { path: 'registration/1', component: UserRegistrationPg1Component },
  { path: 'registration/2', component: UserRegistrationPg2Component },
  { path: 'registration/3', component: UserRegistrationPg3Component },
  { path: 'registration/4', component: UserRegistrationPg4Component },
  { path: 'registration', component: UserRegistrationComponent },
  { path: 'transfer', component: TransferFormComponent },
  { path: 'forgotpassword', component: ForgotPasswordComponent },
  { path: 'forgotpassword/2', component: ForgotPasswordPg2Component },
  { path: 'transferhomepage', component: TransferHomepageComponent },
  { path: 'payee', component: PayeeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
