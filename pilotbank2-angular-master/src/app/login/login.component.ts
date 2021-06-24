import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {takeUntil} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {User} from '../models/user.model';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {AppService} from '../services/app.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  error: boolean;
  resetStatus: boolean;
  locked:boolean;

  constructor(private formBuilder: FormBuilder, private appService: AppService, private http: HttpClient, private router: Router) {
    this.error = false;
  }

  loginForm = new FormGroup({
    clientNumber: new FormControl('', Validators.nullValidator && Validators.required),
    password: new FormControl('', Validators.nullValidator && Validators.required)
  });
  returnUrl: string;
  user = new User();

  ngOnInit(): void {
    this.appService.isOnHomePage(false);
    if(localStorage.getItem("successfulUpdate")!=null)
    {
      this.resetStatus=true;
      localStorage.removeItem("successfulUpdate");
    }
    if(localStorage.getItem("lockedAccount")!=null)
    {
      this.locked=true;
      localStorage.removeItem("lockedAccount");
    }
    this.loginForm = this.formBuilder.group({
      clientNumber: ['', Validators.required],
      password: ['', Validators.required]
    });
    this.returnUrl = '/home';
    this.appService.logout();
  }

  async login(): Promise<void> {
    this.user.username = this.loginForm.value.clientNumber;
    this.user.password = this.loginForm.value.password;
    this.user = await this.appService.authenticate(this.user).catch((err) => { 
      this.user.userId = null;
      return this.user;
  });
    console.log("yoo"+ this.user.username);
    if (this.user.userId == null) {
      this.error = true;
      return;
    }
    localStorage.setItem('isLoggedIn', 'true');
    localStorage.setItem('token', String(this.user.userId));
    localStorage.setItem('user', JSON.stringify(this.user));
    const headers = new Headers();
    headers.append('Authorization', this.user.authToken);
    this.appService.initializeSessionTimeout();
    this.router.navigateByUrl('/home');
  }
}