import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-mailing-information',
  templateUrl: './mailing-information.component.html',
  styleUrls: ['./mailing-information.component.css']
})
export class MailingInformationComponent implements OnInit {
  @Output() mailingInfo = new EventEmitter<{
    streetName: string,
    streetNumber: string,
    suitAppUnitNumber: string,
    province: string,
    cityTown: string,
    postalCode: string
  }>();
  @Input() confirmClicked: boolean;

  newMailingInfo = {
    streetName: '',
    streetNumber: '',
    suitAppUnitNumber: '',
    province: '',
    cityTown: '',
    postalCode: ''
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

  constructor() { }

  ngOnInit(): void {
  }

  addMailingInformation(): void {
    this.mailingInfo.emit({
      streetName: this.newMailingInfo.streetName,
      streetNumber: this.newMailingInfo.streetNumber,
      suitAppUnitNumber: this.newMailingInfo.suitAppUnitNumber,
      province: this.newMailingInfo.province,
      cityTown: this.newMailingInfo.cityTown,
      postalCode: this.newMailingInfo.postalCode
    });
  }

}
