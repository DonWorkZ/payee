import {Component, OnInit} from '@angular/core';
import {RegistrationPg1} from '../models/registrationPg1.model';
import {Customer} from '../models/customer.model';
import {RegistrationPg2} from '../models/registrationPg2.model';
import {Address} from '../models/address.model';
import {Account} from '../models/account.model';
import {AppService} from '../services/app.service';
import {Role} from '../enums/role.enum';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent implements OnInit {

  onPage1 = true;
  onPage2 = false;
  onPage3 = false;
  onPage4 = false;

  customer = new Customer();
  address = new Address();
  account = new Account();

  constructor(private appService: AppService) {
  }

  ngOnInit(): void {
  }

  returnToPage1(backButtonClicked): void{
    if (backButtonClicked){
      this.onPage1 = true;
      this.onPage2 = false;
    }
  }

  returnToPage2(backButtonClicked): void{
    if (backButtonClicked){
      this.onPage2 = true;
      this.onPage3 = false;
    }
  }

  returnToPage3(backButtonClicked): void{
    if (backButtonClicked){
      this.onPage3 = true;
      this.onPage4 = false;
    }
  }

  private createUsername(): string {
    const randomClientNumber = Math.floor(Math.random() * Math.floor(10000)) + 1;
    return randomClientNumber.toString();
  }

  onPage1Completion(registration1: { page1: RegistrationPg1 }): void {
    this.customer.username = this.createUsername();
    this.customer.role = Role.Customer;
    this.customer.isActive = true;
    this.account.accountType = registration1.page1.accountType;
    this.account.balance = registration1.page1.fundingAmount;
    this.customer.account = this.account;
    this.customer.email = registration1.page1.email;
    this.customer.password = registration1.page1.password;
    // this.customer.securityQuestion = registration1.page1.securityQuestion;
    // this.customer.securityAnswer = registration1.page1.securityAnswer;
    if (
      this.customer.account.accountType !== undefined &&
      this.customer.account.balance !== 0 &&
        (this.customer.email !== undefined && this.customer.email !== '') &&
        (this.customer.password !== undefined && this.customer.password !== '') // &&
        // (this.customer.securityQuestion !== undefined && this.customer.securityQuestion !== '') &&
        // (this.customer.securityAnswer !== undefined && this.customer.securityAnswer !== '')
        ){
      this.onPage1 = false;
      this.onPage2 = true;
    }
    console.log(this.customer);
  }

  onPage2Completion(registration2: { page2: RegistrationPg2 }): void {
    this.customer.title = registration2.page2.selectedTitle;
    this.customer.firstName = registration2.page2.firstName;
    this.customer.lastName = registration2.page2.lastName;
    this.customer.phoneNumber = registration2.page2.phoneNumber;
    this.customer.sin = registration2.page2.SIN;
    this.address.streetName = registration2.page2.streetName;
    this.address.streetNumber = registration2.page2.streetNumber;
    this.address.suiteNumber = registration2.page2.suitAppUnitNumber;
    this.address.province = registration2.page2.province;
    this.address.city = registration2.page2.cityTown;
    this.address.postalCode = registration2.page2.postalCode;
    this.customer.address = this.address;
    this.customer.industry = registration2.page2.industry;
    this.customer.occupation = registration2.page2.occupation;
    if (this.customer.title !== undefined &&
        (this.customer.firstName !== undefined && this.customer.firstName !== '') &&
        (this.customer.lastName !== undefined && this.customer.lastName !== '') &&
        (this.customer.phoneNumber !== undefined && this.customer.phoneNumber !== '') &&
        (this.customer.sin !== undefined && this.customer.sin !== '') &&
        (this.customer.address.streetName !== undefined && this.customer.address.streetName !== '') &&
        (this.customer.address.streetNumber !== undefined && this.customer.address.streetNumber !== '') &&
        this.customer.address.province !== undefined &&
        this.customer.address.city !== undefined &&
        (this.customer.address.postalCode !== undefined && this.customer.address.postalCode !== '') &&
        this.customer.industry !== undefined &&
        this.customer.occupation !== undefined){
      this.onPage2 = false;
      this.onPage3 = true;
    }
    console.log(this.customer);
  }

  onPage3Completion(): void {
    this.onPage3 = false;
    this.onPage4 = true;
  }

  onPage4Completion(): void {
    this.appService.register(this.customer);
  }

}
