import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {AppService} from '../services/app.service';

@Component({
  selector: 'app-bank-home',
  templateUrl: './bank-home.component.html',
  styleUrls: ['./bank-home.component.css']
})
export class BankHomeComponent implements OnInit {

  constructor(private appService: AppService) { }

  ngOnInit(): void {
    this.appService.isOnHomePage(true);
  }

}
