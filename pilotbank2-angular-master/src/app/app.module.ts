import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {UserRegistrationComponent} from './user-registration/user-registration.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from './material/material.module';
import {UserRegistrationPg1Component} from './user-registration-pg1/user-registration-pg1.component';
import {UserRegistrationPg2Component} from './user-registration-pg2/user-registration-pg2.component';
import {UserRegistrationPg3Component} from './user-registration-pg3/user-registration-pg3.component';
import {UserRegistrationPg4Component} from './user-registration-pg4/user-registration-pg4.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginComponent} from './login/login.component';
import {HeaderComponent} from './header/header.component';
import {AccountTypeComponent} from './account-type/account-type.component';
import {LoginCredentialsComponent} from './login-credentials/login-credentials.component';
import {SecurityQuestionsComponent} from './security-questions/security-questions.component';
import {CommonModule} from '@angular/common';
import {MailingInformationComponent} from './mailing-information/mailing-information.component';
import {ContactInformationComponent} from './contact-information/contact-information.component';
import {WorkInformationComponent} from './work-information/work-information.component';
import {IdentificationDocumentsComponent} from './identification-documents/identification-documents.component';
import {ConfirmationComponent} from './confirmation/confirmation.component';
import {PrintBarcodeComponent} from './print-barcode/print-barcode.component';
import {HomepageComponent} from './homepage/homepage.component';
import {NotificationsComponent} from './notifications/notifications.component';
import {AccountsListComponent} from './accounts-list/accounts-list.component';
import {AppRoutingModule} from './app-routing.module';
import {AuthGuard} from './gaurds/auth.gaurd';
import {BankHomeComponent} from './bank-home/bank-home.component';
import {RatesComponent} from './rates/rates.component';
import {ClientsComponent} from './clients/clients.component';
import {ArticlesComponent} from './articles/articles.component';
import {TransactionComponent} from './transaction/transaction.component';
import {AccountItemService} from './services/account-item.service';
import {SummaryComponent} from './summary/summary.component';
import {OpenNewAccountModalComponent} from './open-new-account-modal/open-new-account-modal.component';
import {ProfilePageComponent} from './profile-page/profile-page.component';
import {ProfileInformationComponent} from './profile-information/profile-information.component';
import {StatementsAndDocumentsComponent} from './statements-and-documents/statements-and-documents.component';
import {MatPaginatorModule} from '@angular/material/paginator';
import { TransferFormComponent } from './transfer-form/transfer-form.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ForgotPasswordPg2Component } from './forgot-password-pg2-verify-securitycode/forgot-password-pg2.component';
import { TransferHomepageComponent } from './transfer-homepage/transfer-homepage.component';
import { PayeeComponent } from './payee/payee.component';
@NgModule({
  declarations: [
    AppComponent,
    UserRegistrationComponent,
    UserRegistrationPg1Component,
    UserRegistrationPg2Component,
    UserRegistrationPg3Component,
    UserRegistrationPg4Component,
    LoginComponent,
    HeaderComponent,
    AccountTypeComponent,
    LoginCredentialsComponent,
    SecurityQuestionsComponent,
    MailingInformationComponent,
    ContactInformationComponent,
    WorkInformationComponent,
    IdentificationDocumentsComponent,
    ConfirmationComponent,
    PrintBarcodeComponent,
    HomepageComponent,
    NotificationsComponent,
    AccountsListComponent,
    BankHomeComponent,
    RatesComponent,
    ClientsComponent,
    ArticlesComponent,
    TransactionComponent,
    SummaryComponent,
    OpenNewAccountModalComponent,
    ProfilePageComponent,
    ProfileInformationComponent,
    StatementsAndDocumentsComponent,
    TransferFormComponent,
    ForgotPasswordComponent,
    ForgotPasswordPg2Component,
    TransferHomepageComponent,
    PayeeComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MaterialModule,
    FormsModule,
    CommonModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatPaginatorModule
  ],
  providers: [AuthGuard, AccountItemService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
