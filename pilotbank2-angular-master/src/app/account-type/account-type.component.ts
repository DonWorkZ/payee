import {Component, OnInit, EventEmitter, Output, Input} from '@angular/core';
import {AccountType} from '../enums/accountType.enum';

@Component({
  selector: 'app-account-type',
  templateUrl: './account-type.component.html',
  styleUrls: ['./account-type.component.css']
})
export class AccountTypeComponent implements OnInit {
  @Output() selectedAccountType = new EventEmitter<{ accountType: string, fundingAmount: number }>(); // to be edited
  @Input() confirmClicked: boolean;

  accountTypes = [
    'Chequing Account',
    'Savings Account',
    'Student Account',
    'First Class Chequing Account',
    'Premium Visa Account',
    'Business Visa Account'
  ];
  accountMap = new Map([
    ['Chequing Account', AccountType.Checking],
    ['Savings Account', AccountType.Savings],
    ['Student Account', AccountType.Student],
    ['First Class Chequing Account', AccountType.FirstClassChecking],
    ['Premium Visa Account', AccountType.PremiumVisa],
    ['Business Visa Account', AccountType.BusinessVisa]
  ]);

  newSelectedAccountType = '';
  newFundingAmount = 0; // to be deleted

  constructor() {
  }

  ngOnInit(): void {
  }

  onSelectAndFund(): void {
    if (this.newSelectedAccountType !== undefined && this.newFundingAmount !== 0){
      this.selectedAccountType.emit({
        accountType: this.accountMap.get(this.newSelectedAccountType).toUpperCase(),
        fundingAmount : this.newFundingAmount // to be deleted
      });
    }
  }

}
