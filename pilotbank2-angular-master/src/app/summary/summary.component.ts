import {Component, OnInit} from '@angular/core';
import {AccountItemService} from '../services/account-item.service';
import {Observable} from 'rxjs';
import {Account} from '../models/account.model';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.css']
})
export class SummaryComponent implements OnInit {

  account$: Observable<Account>;
  showAccountInformation = false;

  constructor(private accountItemService: AccountItemService) {
  }

  ngOnInit(): void {
    this.account$ = this.accountItemService.getAccountItem();
  }

  openNav(): void {
    document.getElementById('mySidenav').style.width = '250px';
    document.getElementById('main').style.marginLeft = '250px';
    document.getElementById('menu').style.display = 'none';
  }

  formatAccountBalance(balance: number): string {
    return balance.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

}
