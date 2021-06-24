import {EventEmitter, Injectable, Output} from '@angular/core';
import {Observable, of} from 'rxjs';
import {Account} from '../models/account.model';


@Injectable({
  providedIn: 'root'
})
export class AccountItemService {

  @Output() accountItem: EventEmitter<Account> = new EventEmitter();

  getAccountItem(): Observable<Account> {
    const clickedAccount = localStorage.getItem('clickedAccount');
    if (clickedAccount) {
      return of(JSON.parse(clickedAccount) as Account);
    }
  }


}
