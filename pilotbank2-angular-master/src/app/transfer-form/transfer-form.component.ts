import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Transfer } from '../transfer';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../models/user.model';
import { AppService } from '../services/app.service';


@Component({
  selector: 'app-transfer-form',
  templateUrl: './transfer-form.component.html',
  styleUrls: ['./transfer-form.component.css']
})
export class TransferFormComponent implements OnInit {

  user: User;
  accountList = [];
  wSelected:string;
  min:number = 0;
  max:number = 400;

  transfer: Transfer = new Transfer(0,0,0);

  transferForm = new FormGroup({
    fromAccountId: new FormControl('', Validators.nullValidator && Validators.required),
    toAccountId: new FormControl('', Validators.nullValidator && Validators.required),
    transferAmount: new FormControl('', Validators.nullValidator && Validators.required)
  });

  constructor(private formBuilder: FormBuilder, private http: HttpClient, private router: Router, private appService: AppService) { }
  ngOnInit(): void {
    this.user = JSON.parse(localStorage.getItem('user'));
    if(this.user){
      console.log(this.user);
      this.accountList = this.user.ownedAccounts;
    }
    else{
      console.log("no user");
      //this.router.navigateByUrl('/home');
    }
    //set the accounts and min/max based on user accounts
    this.transferForm = this.formBuilder.group({
      fromAccountId: ['', Validators.required],
      toAccountId: ['', Validators.required],
      transferAmount: ['', Validators.required]
    });
  }

  showMsg: boolean = false;

  onSubmit(){
    console.log("submit clicked");
    let fromAccountIdElm: any = document.getElementById('fromAccountId');
    this.transfer.fromAccountId = fromAccountIdElm?.value || 0;

    let toAccountIdElm: any = document.getElementById('toAccountId');
    this.transfer.toAccountId = toAccountIdElm?.value || 0;

    if(this.transfer.fromAccountId != null && this.transfer.fromAccountId != 0 &&
      this.transfer.fromAccountId != null && this.transfer.fromAccountId != 0 &&
      this.transfer.transferAmount != null && this.transfer.transferAmount != 0){
        this.appService.transfer(this.transfer).then((res: any) => {
          this.updateAccounts(res);
          if (Object.keys(res).length) {
            this.user.ownedAccounts = res;
          }
          this.showMsg = true;
        });
        console.log("submited: " + JSON.stringify(this.transfer));
        console.log("transfer from" + this.transfer.fromAccountId);
        console.log("transfer to" + this.transfer.toAccountId);
      }
  }

  updateAccounts(data: any) {
    this.user.ownedAccounts = data;
    localStorage.setItem('user', JSON.stringify(this.user));
    this.accountList = data;
  }

  @Output() change = new EventEmitter<Event>();

}
