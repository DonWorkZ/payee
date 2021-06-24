import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {RegistrationPg2} from '../models/registrationPg2.model';

@Component({
  selector: 'app-user-registration-pg2',
  templateUrl: './user-registration-pg2.component.html',
  styleUrls: ['./user-registration-pg2.component.css']
})
export class UserRegistrationPg2Component implements OnInit {
  @Output() registration2 = new EventEmitter<{ page2: RegistrationPg2 }>();
  @Output() goBack = new EventEmitter<boolean>();

  page2 = new RegistrationPg2();
  confirmed = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.confirmed = true;
    this.registration2.emit({page2: this.page2});
  }

  back(): void{
    this.goBack.emit(true);
  }

  onContactInformationAdded(contactInfo: {
    selectedTitle: string,
    firstName: string,
    lastName: string,
    phoneNumber: string,
    SIN: string
  }): void {
    this.page2.selectedTitle = contactInfo.selectedTitle;
    this.page2.firstName = contactInfo.firstName;
    this.page2.lastName = contactInfo.lastName;
    this.page2.phoneNumber = contactInfo.phoneNumber;
    this.page2.SIN = contactInfo.SIN;
  }

  onMailingInformationAdded(mailingInfo: {
    streetName: string,
    streetNumber: string,
    suitAppUnitNumber: string,
    province: string,
    cityTown: string,
    postalCode: string
  }): void {
    this.page2.streetName = mailingInfo.streetName;
    this.page2.streetNumber = mailingInfo.streetNumber;
    this.page2.suitAppUnitNumber = mailingInfo.suitAppUnitNumber;
    this.page2.province = mailingInfo.province;
    this.page2.cityTown = mailingInfo.cityTown;
    this.page2.postalCode = mailingInfo.postalCode;
  }

  onWorkInformationAdded(workInfo: {
    industry: string,
    occupation: string
  }): void {
    this.page2.industry = workInfo.industry;
    this.page2.occupation = workInfo.occupation;
  }

}
