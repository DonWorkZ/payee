import {Component, OnInit, Output, ViewChild} from '@angular/core';
import {Account} from '../models/account.model';
import {User} from '../models/user.model';
import {AccountItemService} from '../services/account-item.service';
import {Router} from '@angular/router';
import {EventEmitter} from 'events';
import {OpenNewAccountModalComponent} from '../open-new-account-modal/open-new-account-modal.component';
import {AppService} from '../services/app.service';

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.css']
})
export class AccountsListComponent implements OnInit {

  @ViewChild(OpenNewAccountModalComponent) openNewAccountComponent: OpenNewAccountModalComponent;

  user: User;
  accountList: Account[];
  accountTypeMap = new Map([
    ['CHECKING', 'Chequing'],
    ['SAVINGS', 'Savings'],
    ['STUDENT', 'Student'],
    ['FIRST_CLASS_CHECKING', 'First Class Chequing'],
    ['PREMIUM_VISA', 'Premium Visa'],
    ['BUSINESS_VISA', 'Business Visa']
  ]);

  constructor(private accountItemService: AccountItemService, private appService: AppService, private router: Router) {
  }

  ngOnInit(): void {
    this.user = this.appService.getUser();
    console.log(this.user);
    if (this.user && this.user.ownedAccounts) {
      // this.accountList = newArray<Account>(this.user.ownedAccounts.length);
      this.accountList = this.user.ownedAccounts;
      this.accountList.forEach((account) => {
        account.accountType = this.accountTypeMap.get(account.accountType);
      });
      console.log(this.accountList.length);
    }
  }

  selectAccount( accountItem: Account ): void{
    this.accountItemService.accountItem.emit(accountItem);
    localStorage.setItem('clickedAccount', JSON.stringify(accountItem));
    console.log('accountItem:');
    console.log(accountItem);
    this.router.navigateByUrl('/transaction');
  }

  display(): void{
    this.openNewAccountComponent.displayModal();
  }

}
