import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {takeUntil} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {User} from '../models/user.model';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {AppService} from '../services/app.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  error: boolean;

  constructor(private formBuilder: FormBuilder, private appService: AppService, private http: HttpClient, private router: Router) {
    this.error = false;
  }

  forgotForm = new FormGroup({
    clientNumber: new FormControl('', Validators.nullValidator && Validators.required)
  });

  answerForm = new FormGroup({
    answer: new FormControl('', Validators.nullValidator && Validators.required)
  });
  returnUrl: string;
  question: string;
  answer: string;
  user = new User();
  isShow = false;
  now: number;
  attempt = 3;
  verified: string;

  ngOnInit(): void {
    this.appService.isOnHomePage(false);
    this.forgotForm = this.formBuilder.group({
      clientNumber: ['', Validators.required]
    });
    this.answerForm = this.formBuilder.group({
      answer: ['', Validators.required]
    });
  }

  async forgotPassword(): Promise<void> {
    this.user.username = this.forgotForm.value.clientNumber;
    console.log("hi");
    this.question= await this.appService.getQuestion(this.user.username);
    this.toggleDisplay();
  }

  async answerQuestion()
  {
    this.answer= this.answerForm.value.answer;
    this.verified = await this.appService.verifyAnswer(this.user.username,this.answer)
    console.log(this.verified);
    if (this.verified == 'Correct') {
      console.log("good standing");
      localStorage.setItem('usernameForgotPassword',this.user.username);
      this.router.navigateByUrl('/forgotpassword/2');
    }
    //need to check for failure 1 and failure 2 then blocked on third attempt
    else if(this.verified=='Incorrect Answer')
    {
      if(this.attempt==1)
      {
        localStorage.setItem('lockedAccount','locked');
        console.log("Account is locked. Please contact our administration.");
        this.router.navigateByUrl('/home');
      }
      else if(this.attempt==3)
      {
        this.now = Date.now()+(1000*90);
      }
      else if(this.attempt==2)
      {
        this.now = Date.now()+(1000*150);
      }
      this.attempt=this.attempt-1;
      this.error=true;
    }
    else if(this.verified=='Account Locked')
    {
      localStorage.setItem('lockedAccount','locked');
      console.log("Account is locked. Please contact our administration.");
      this.router.navigateByUrl('/home');
    }
  }

  toggleDisplay() {
    if(this.question!=null)
    {
      this.error=false;
      this.isShow = !this.isShow;
    }
    else
    {
      console.log(this.question);
      console.log("here no");
      this.error=true;
    }
}

}