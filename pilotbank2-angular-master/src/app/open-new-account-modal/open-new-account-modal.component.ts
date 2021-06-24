import { Component, OnInit } from '@angular/core';
import {AppService} from '../services/app.service';
import {AccountType} from '../enums/accountType.enum';
import {AccountCategory} from '../enums/accountCategory.enum';

@Component({
  selector: 'app-open-new-account-modal',
  templateUrl: './open-new-account-modal.component.html',
  styleUrls: ['./open-new-account-modal.component.css']
})
export class OpenNewAccountModalComponent implements OnInit {

  selectedAccountCategory: AccountCategory;
  newSelectedAccountType: AccountType;

  constructor(private appService: AppService) { }

  ngOnInit(): void {
  }

  public get accountCategory(): typeof AccountCategory {
    return AccountCategory;
  }

  public get accountType(): typeof AccountType {
    return AccountType;
  }

  displayModal(): void{
    document.getElementById('myModal').style.display = 'block';
  }

  hideModal(): void{
    document.getElementById('myModal').style.display = 'none';
  }

  async openNewAccount(): Promise<void>{
    this.appService.createNewAccount(this.newSelectedAccountType);
  }

  chooseAccount(selectedAccountCategory: AccountCategory): any{
    this.selectedAccountCategory = selectedAccountCategory;
    this.newSelectedAccountType = null;
  }

}
