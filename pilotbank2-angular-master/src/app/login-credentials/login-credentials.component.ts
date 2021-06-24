import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import { AppService } from '../services/app.service';

@Component({
  selector: 'app-login-credentials',
  templateUrl: './login-credentials.component.html',
  styleUrls: ['./login-credentials.component.css']
})
export class LoginCredentialsComponent implements OnInit {
  @Output() loginCredentials = new EventEmitter<{ email: string, password: string }>();
  @Input() confirmClicked: boolean;

  error: boolean;
  isEmailValidated = true;

  newLoginCredentials = {
    email: {emailLabel: 'Email', emailAddress: ''},
    emailConfirmed: {confirmedEmailLabel: 'Confirm Email', confirmedEmailAddress: '', isEmailValidated: true},
    password: {passwordLabel: 'Password', passwordEntered: ''},
    passwordConfirmed: {confirmPassword: 'Confirm Password', confirmPasswordEntered: ''}
  };

  constructor(private appService: AppService) {
  }

  ngOnInit(): void {
  }

  validateEmail(): boolean{
    const controlEmail = new FormControl(this.newLoginCredentials.email.emailAddress, Validators.email);

    if (controlEmail.errors){
      return false;
    }
    return true;
  }

  addLoginCredentials(): void {
    this.isEmailValidated = this.validateEmail();
    if (this.newLoginCredentials.email.emailAddress === this.newLoginCredentials.emailConfirmed.confirmedEmailAddress &&
        this.newLoginCredentials.password.passwordEntered === this.newLoginCredentials.passwordConfirmed.confirmPasswordEntered &&
        this.newLoginCredentials.email.emailAddress !== undefined &&
        this.isEmailValidated &&
        this.newLoginCredentials.password.passwordEntered !== undefined) {
        if(this.appService.verifyPasswordConstraints(this.newLoginCredentials.password.passwordEntered)==true){
          this.error=false;
          this.loginCredentials.emit({
        email: this.newLoginCredentials.emailConfirmed.confirmedEmailAddress,
        password: this.newLoginCredentials.passwordConfirmed.confirmPasswordEntered
      });
    }
        else
        {
          this.error=true;
        }
    }
  }

}
