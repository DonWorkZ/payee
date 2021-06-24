import {Component, Input, OnInit} from '@angular/core';
import {AppService} from '../services/app.service';
import {Observable, of} from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  loggedIn$: Observable<boolean>;
  isOnHomePage$: Observable<boolean>;

  constructor(private app: AppService) {
    app.getIsLoggedIn.subscribe(login => this.loggedIn$ = of(login));
    this.app.getIsOnHomePage.subscribe(isOnHomePage => {
      this.isOnHomePage$ = of(isOnHomePage);
    });
    /*console.log(this.isOnHomePage$);*/
  }

  ngOnInit(): void {
    this.loggedIn$ = this.app.isAuthenticated();
    console.log(this.loggedIn$);
  }

  logOut(): void {
    this.app.logout();
  }


}
