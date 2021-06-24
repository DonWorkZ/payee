import {Component, OnInit} from '@angular/core';
import {AppService} from '../services/app.service';
import {Observable, of} from 'rxjs';
import {AccountItemService} from '../services/account-item.service';
import {Account} from '../models/account.model';
import {Transaction} from '../models/transaction.model';
import {KeyValue} from '@angular/common';
import {AccountType} from '../enums/accountType.enum';
import { Router } from '@angular/router';

@Component({
  selector: 'app-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.css']
})
export class TransactionComponent implements OnInit {

  account$: Observable<Account>;
  transactionMap: Map<string, Array<Transaction>> = new Map<string, Array<Transaction>>();
  dateArray: Array<string>;
  reverseMap = (a: KeyValue<string, Array<Transaction>>, b: KeyValue<string, Array<Transaction>>): number => {
    return a.key > b.key ? -1 : (b.key > a.key ? 1 : 0);
  }

  constructor(private accountItemService: AccountItemService, private router: Router) {
    this.accountItemService.accountItem.subscribe(accountItem => {
      this.account$ = of(accountItem);
    });
  }

  ngOnInit(): void {
    this.account$ = this.accountItemService.getAccountItem();
    this.formatTransactions();
  }

  openNav(): void {
    document.getElementById('mySidenav').style.width = '250px';
    document.getElementById('main').style.marginLeft = '250px';
    document.getElementById('menu').style.display = 'none';
  }

  formatTransactions(): void {
    // this logic is grouping transactions by date and formatting the transaction data
    const account = JSON.parse(localStorage.getItem('clickedAccount')) as Account;
    const transactions = account.allTransactions;
    this.dateArray = new Array<string>();
    for (let i = 0; i <= transactions.length - 1; i++) {
      transactions[i].amount = this.formatAmount(account.accountType, transactions[i]);
      transactions[i].transactionMemo = this.formatMemo(transactions[i].transactionMemo);
      const date = transactions[i].transactionDate.substring(0, 10);
      if (this.transactionMap.has(date)) {
        this.transactionMap.get(date).push(transactions[i]);
      } else {
        const array: Transaction[] = [transactions[i]];
        this.transactionMap.set(date, array);
        this.dateArray.push(date);
      }
    }
  }

  formatMemo(transactionMemo: string): string {
    let memo = transactionMemo.charAt(0) + transactionMemo.substring(1).toLowerCase();
    if (memo.includes('_')) {
      memo = memo.replace('_', ' ');
    }
    return memo;
  }

  formatAmount(accountType: string, transaction: Transaction): string {
    let formattedAmount = '';
    if (accountType === AccountType.PremiumVisa || accountType === AccountType.BusinessVisa) {
      if (transaction.transactionType === 'DEBIT') {
        transaction.isPositive = true;
        formattedAmount = '+$' + transaction.amount;
        return formattedAmount;
      }
      transaction.isPositive = false;
      formattedAmount = '-$' + transaction.amount;
      return formattedAmount;
    } else {
      if (transaction.transactionType === 'DEBIT') {
        transaction.isPositive = false;
        formattedAmount = '-$' + transaction.amount;
        return formattedAmount;
      }
      transaction.isPositive = true;
      formattedAmount = '+$' + transaction.amount;
      return formattedAmount;
    }
  }

  formatAccountBalance(balance: number): string {
    return balance.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

}
