import { Component, OnInit } from '@angular/core';
import {User} from '../models/user.model';
import {AppService} from '../services/app.service';

@Component({
  selector: 'app-profile-information',
  templateUrl: './profile-information.component.html',
  styleUrls: ['./profile-information.component.css']
})
export class ProfileInformationComponent implements OnInit {

  user: User;
  contactEdit: boolean;
  addressEdit: boolean;

  updatedContactInfo = {
    email: '',
    phoneNumber: ''
  };

  updatedAddressInfo = {
    address: {
      streetNumber: '',
      streetName: '',
      suitAppUnitNumber: '',
      city: '',
      province: '',
      postalCode: ''
    }
  };

  provinces = [
    'AB',
    'ON',
    'BC',
    'MB',
    'NB'
  ];

  cities = [
    'Edmonton',
    'Ottawa',
    'Victoria',
    'Toronto',
    'Halifax'
  ];

  constructor(private appService: AppService) {
  }

  ngOnInit(): void {
    this.user = this.appService.getUser();
    this.updatedContactInfo.email = this.user.email;
    this.updatedContactInfo.phoneNumber = this.user.phoneNumber;
    this.updatedAddressInfo.address.streetNumber = this.user.addressList[0].streetNumber;
    this.updatedAddressInfo.address.streetName = this.user.addressList[0].streetName;
    this.updatedAddressInfo.address.suitAppUnitNumber = this.user.addressList[0].suiteNumber;
    this.updatedAddressInfo.address.city = this.user.addressList[0].city;
    this.updatedAddressInfo.address.province = this.user.addressList[0].province;
    this.updatedAddressInfo.address.postalCode = this.user.addressList[0].postalCode;
  }

  async updateContactInfo(): Promise<void> {
    console.log(this.updatedContactInfo);
    this.user = await this.appService.updateUser(this.updatedContactInfo);
    this.contactEdit = false;
  }

  async updateAddressInfo(): Promise<void> {
    console.log(this.updatedAddressInfo);
    this.user = await this.appService.updateUser(this.updatedAddressInfo);
    this.addressEdit = false;
  }



}
