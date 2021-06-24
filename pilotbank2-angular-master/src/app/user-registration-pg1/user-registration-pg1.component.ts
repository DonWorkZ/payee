import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {RegistrationPg1} from '../models/registrationPg1.model';

@Component({
  selector: 'app-user-registration-pg1',
  templateUrl: './user-registration-pg1.component.html',
  styleUrls: ['./user-registration-pg1.component.css']
})
export class UserRegistrationPg1Component implements OnInit {
  @Output() registration1 = new EventEmitter<{ page1: RegistrationPg1 }>();

  page1 = new RegistrationPg1();
  confirmed = false;

  constructor() {
  }

  onSubmit(): void {
    this.confirmed = true;
    this.registration1.emit({page1: this.page1});
  }

  ngOnInit(): void {
  }

  onAccountTypeAdded(account: {accountType: string, fundingAmount: number}): void {
    this.page1.accountType = account.accountType;
    this.page1.fundingAmount = account.fundingAmount;
  }

  onLoginCredentialsAdded(loginCredentials: { email: string, password: string }): void {
    this.page1.email = loginCredentials.email;
    this.page1.password = loginCredentials.password;
  }

  onSecurityQuestionAdded(securityQuestion: { question: string, answer: string }): void {
    this.page1.securityQuestion = securityQuestion.question;
    this.page1.securityAnswer = securityQuestion.answer;
  }
}
