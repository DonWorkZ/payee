import {Component, OnDestroy, OnInit} from '@angular/core';
import {AppService} from '../services/app.service';

@Component({
  selector: 'app-transfer-homepage',
  templateUrl: './transfer-homepage.component.html',
  styleUrls: ['./transfer-homepage.component.css']
})
export class TransferHomepageComponent implements OnInit {

  constructor(private appService: AppService) {
  }

  ngOnInit(): void {
    this.appService.initializeSessionTimeout();
  }

  openNav(): void {
    document.getElementById('mySidenav').style.width = '250px';
    document.getElementById('main').style.marginLeft = '250px';
    document.getElementById('menu').style.display = 'none';
  }

}