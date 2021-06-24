import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-user-registration-pg4',
  templateUrl: './user-registration-pg4.component.html',
  styleUrls: ['./user-registration-pg4.component.css']
})
export class UserRegistrationPg4Component implements OnInit {
  @Output() registration4 = new EventEmitter();
  @Output() goBack = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.registration4.emit();
  }

  back(): void{
    this.goBack.emit(true);
  }

}
