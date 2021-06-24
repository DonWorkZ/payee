import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {takeUntil} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {User} from '../models/user.model';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {AppService} from '../services/app.service';

@Component({
  selector: 'app-forgot-password-pg2=verify-securitycode',
  templateUrl: './forgot-password-pg2.component.html',
  styleUrls: ['./forgot-password-pg2.component.css']
})
export class ForgotPasswordPg2Component implements OnInit {

    error: boolean;
    error1: boolean;
    isShow = true;
  
    constructor(private formBuilder: FormBuilder, private appService: AppService, private http: HttpClient, private router: Router) {
      this.error = false;
    }
  
    validateCodeForm = new FormGroup({
        securityCode: new FormControl('', Validators.nullValidator && Validators.required)
    });

    updateForm = new FormGroup({
      password: new FormControl('', Validators.nullValidator && Validators.required),
      confirmPassword: new FormControl('', Validators.nullValidator && Validators.required)
    });


    returnUrl: string;
    securityCode: number;
    user = new User();
  
    ngOnInit(): void {
      this.appService.isOnHomePage(false);
      if(localStorage.getItem("usernameForgotPassword")!=null)
      {
        this.user.username=localStorage.getItem("usernameForgotPassword");
        localStorage.removeItem("usernameForgotPassword");
        this.validateCodeForm = this.formBuilder.group({
          securityCode: ['', Validators.required]
        });
        this.updateForm = this.formBuilder.group({
          password: ['', Validators.required],
          confirmPassword: ['', Validators.required]
        });
      }
      else{
        this.router.navigateByUrl('/home');
      }
    }
  
    async checkCode()
    {
      this.securityCode = this.validateCodeForm.value.securityCode;
      if(await this.appService.verifySecurityCode(this.user.username,this.securityCode))
      {
        this.toggleDisplay();
        this.error=false;
      }
      else{
        console.log('Security Code Incorrect');
        this.error=true;
      }
    }

    async updatePassword()
    {
      if(this.updateForm.value.confirmPassword==this.updateForm.value.password)
      {
        this.user.password=this.updateForm.value.confirmPassword;
        let updateStatus=await this.appService.updatePassword(this.user.username,this.user.password)
        console.log(updateStatus);
        if(updateStatus)
        {
          this.error=false;
          this.error1=false;
          localStorage.setItem("successfulUpdate","Your Password was successfully updated");
          this.router.navigateByUrl('/home');
        }
        else
        {
          this.error=false;
          this.error1=true;
        }
      }
      else
      {
        this.error1=false;
        this.error=true;
      }
    }
  
    toggleDisplay() {
      this.isShow = !this.isShow;
  }
  }