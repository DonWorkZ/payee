import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-user-registration-pg3',
  templateUrl: './user-registration-pg3.component.html',
  styleUrls: ['./user-registration-pg3.component.css']
})
export class UserRegistrationPg3Component implements OnInit {
  @Output() registration3 = new EventEmitter();
  @Output() goBack = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.registration3.emit();
  }

  back(): void{
    this.goBack.emit(true);
  }

}
