import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-work-information',
  templateUrl: './work-information.component.html',
  styleUrls: ['./work-information.component.css']
})
export class WorkInformationComponent implements OnInit {
  @Output() workInfo = new EventEmitter<{
    industry: string,
    occupation: string
  }>();
  @Input() confirmClicked: boolean;

  newWorkInfo = {
    industry: '',
    occupation: ''
  };

  industries = [
    'Education',
    'Construction',
    'Energy',
    'Medical',
    'Technology',
  ];

  occupations = [
    'Engineering',
    'Nurse',
    'Doctor',
    'IT',
    'Sales',
    'HR',
    'Teacher',
  ];

  constructor() {
  }

  ngOnInit(): void {
  }

  addWorkInformation(): void {
    this.workInfo.emit({
      industry: this.newWorkInfo.industry,
      occupation: this.newWorkInfo.occupation
    });
  }

}
