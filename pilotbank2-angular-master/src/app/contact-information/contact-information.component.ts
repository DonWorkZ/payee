import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-contact-information',
  templateUrl: './contact-information.component.html',
  styleUrls: ['./contact-information.component.css']
})
export class ContactInformationComponent implements OnInit {
  @Output() contactInfo = new EventEmitter<{
    selectedTitle: string,
    firstName: string,
    lastName: string,
    phoneNumber: string,
    SIN: string
  }>();
  @Input() confirmClicked: boolean;

  titles = ['Mr', 'Ms', 'Mrs'];

  newContactInfo = {
    titleSelected: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    SIN: ''
  };

  constructor() {
  }

  ngOnInit(): void {
  }

  addContactInformation(): void {
    this.contactInfo.emit({
      selectedTitle: this.newContactInfo.titleSelected,
      firstName: this.newContactInfo.firstName,
      lastName: this.newContactInfo.lastName,
      phoneNumber: this.newContactInfo.phoneNumber,
      SIN: this.newContactInfo.SIN
    });
  }

}
