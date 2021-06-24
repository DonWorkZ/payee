import {EventEmitter, Injectable, NgZone, OnDestroy, OnInit, Output} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, forkJoin, interval, Observable, of} from 'rxjs';
import {User} from '../models/user.model';
import jwt_decode, {JwtPayload} from 'jwt-decode';
import {Router} from '@angular/router';
import {map, takeWhile} from 'rxjs/operators';
import {AccountType} from '../enums/accountType.enum';
import {Account} from '../models/account.model';
import {AccountCreateRequest} from '../models/accountCreateRequest.model';
import {Transaction} from '../models/transaction.model';
import {StatementList} from '../models/statementList.model';
import { Transfer } from '../transfer';


const STORE_KEY = 'userLastAction';

@Injectable({
  providedIn: 'root'
})
export class AppService implements OnDestroy {

  public static runTimer: boolean;

  @Output() getIsLoggedIn: EventEmitter<any> = new EventEmitter();

  @Output() getIsOnHomePage: EventEmitter<any> = new EventEmitter();

  public USER_IDLE_TIMER_VALUE_IN_MIN = 15;
  public userIdlenessChecker: BehaviorSubject<string>;

  private sessionForIdle: Observable<number>;
  private userActivityChangeCallback: ($event) => void;

  public clockForIdle: Observable<number>;

  private authenticateUrl: string;
  private loginUrl: string;
  private registerUrl: string;
  private createNewAccountUrl: string;
  private statementsUrl: string;
  private transferUrl: string;
  private getQuestionUrl: string;
  private getAnswerUrl: string;
  private sendSecurityCodeUrl: string;
  private verifySecurityCodeUrl: string;
  private updatePasswordUrl: string;

    // authenticated: Observable<boolean> = new BehaviorSubject<boolean>(false);
    user: User = new User();
    authToken = '';


  constructor(private http: HttpClient, private router: Router, private zone: NgZone) {
    if (!this.userIdlenessChecker) {
      this.userIdlenessChecker = new BehaviorSubject<string>('INITIATE_TIMER');
    }
    this.authenticateUrl = 'http://localhost:8082/auth/login';
    this.loginUrl = 'http://localhost:8082/users/';
    this.registerUrl = 'http://localhost:8082/users/customers/create';
    this.createNewAccountUrl = 'http://localhost:8082/accounts/create';
    this.statementsUrl = 'http://localhost:8082/transactions/statementList?accountId=';
    this.transferUrl = 'http://localhost:8082/transactions/transfer';
    this.getQuestionUrl = 'http://localhost:8082/auth/initiateTwoStepVerification';
    this.getAnswerUrl = 'http://localhost:8082/auth/answerSecurityQuestion';
    this.sendSecurityCodeUrl = 'http://localhost:8082/auth/requestSecurityCode';
    this.verifySecurityCodeUrl ='http://localhost:8082/auth/verifySecurityCode';
    this.updatePasswordUrl ='http://localhost:8082/auth/updatePassword';
  }

  verifyPasswordConstraints(password: string)
  {
    if(password.length>=8)
    {
      console.log("here");
      var regex = new RegExp("[a-zA-Z]+"); // Check for letters  
        if(regex.test(password) == true) 
        {
            regex = new RegExp("[0-9]+"); // Now we check for numbers   
            if(regex.test(password) == true) 
            {
                //return true;
                regex = new RegExp("[!@#\\$%^&*()_+\\-={};':<>?~]+"); // checking now special characters
                if(regex.test(password) == true) 
                {
                    return true;   
                }
            }
        }
    }
    return false;
  }

  async updatePassword(username: any, password: any): Promise<boolean>
  {
    var body = JSON.stringify({username,password});
    console.log(body);
    const res = await this.http.post(this.updatePasswordUrl, body, {headers: new HttpHeaders({'Content-Type': 'application/json'}), responseType: 'text'})
    .toPromise().catch((err) => { 
      return false;
  });
    console.log(res);
    if (res=='Password Updated Successfully')
    {
      console.log("hererr");
      return true;
    }
    else{
      return false;
    }
  }

  async verifySecurityCode(username: any,securityCode: any): Promise<boolean>
  {
    var body = JSON.stringify({username,securityCode});
    console.log(body);
    const res = await this.http.post(this.verifySecurityCodeUrl, body, {headers: new HttpHeaders({'Content-Type': 'application/json'}), responseType: 'text'}).toPromise();
    if (res=='Security Code Verified Successfully')
    {
      return true;
    }
    else{
      return false;
    }
  }

  async verifyAnswer(username: any,answer: any): Promise<string>
  {
    var body = JSON.stringify({username,answer});
    console.log(body);
    const res = await this.http.post(this.getAnswerUrl, body, {headers: new HttpHeaders({'Content-Type': 'application/json'})}).toPromise();
    if(res[0]=='Correct')
    {
      var usernameBody=JSON.stringify(username);
      await this.http.post(this.sendSecurityCodeUrl, usernameBody, {headers: new HttpHeaders({'Content-Type': 'application/json'}), responseType: 'text'}).toPromise();
    }
    return res[0];
  }


  async getQuestion(username: any): Promise<string>
  {
    var body = JSON.stringify(username);

    const question = await this.http.post(this.getQuestionUrl, body, {headers: new HttpHeaders({'Content-Type': 'application/json'}), responseType: 'text'})
    .toPromise().catch((err) => { 
      return null;
  });;
    console.log(question);
    return question;

  }

  async transfer(updatedInfo: any){
    this.user = this.getUser();
    if (this.user.authToken){
      this.authToken = this.user.authToken;
      return await this.http.put<Transfer>(this.transferUrl, updatedInfo, {
        headers: new HttpHeaders({Authorization: this.authToken})
      }).toPromise();
    }
  }


  isOnHomePage(isOnHomePage: boolean): Observable<boolean> {
    if (isOnHomePage === true) {
      this.getIsOnHomePage.emit(true);
      this.getIsOnHomePage.next(isOnHomePage);
      return of(true);
    } else {
      this.getIsOnHomePage.emit(false);
      this.getIsOnHomePage.next(isOnHomePage);
      return of(false);
    }
  }

  isAuthenticated(): Observable<boolean> {
    if (localStorage.getItem('isLoggedIn') === 'true') {
      return of(true);
    } else {
      return of(false);
    }
  }

  getUser(): User {
    this.user = JSON.parse(localStorage.getItem('user')) as User;
    return this.user;
  }

  logout(): void {
    localStorage.setItem('isLoggedIn', 'false');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem(STORE_KEY);
    localStorage.removeItem('clickedAccount');
    this.getIsLoggedIn.emit(false);
    this.router.navigateByUrl('/login');
  }

  async authenticate(user: any): Promise<User> {
    type customJwtPayload = JwtPayload & { userId: string };
    console.log("What I Need 1st ");
    let responseJwt = await this.http.post(this.authenticateUrl, user, {responseType: 'text'})
        .toPromise();
    console.log("What I Need "+ responseJwt);
    if (responseJwt) {
      this.authToken = responseJwt;
      const decodedJwt = jwt_decode<customJwtPayload>(responseJwt.split(' ')[1]);
      this.user = await this.http.get<User>(this.loginUrl + decodedJwt.userId, {
        headers: new HttpHeaders({Authorization: this.authToken})
      }).toPromise();
      this.user.authToken = this.authToken;
      this.getIsLoggedIn.emit(true);
    } else {
      this.getIsLoggedIn.emit(false);
    }
    return this.user;
  }

  async updateUser(updatedInfo: any): Promise<User> {
    this.user = this.getUser();
    if (this.user.authToken){
      this.authToken = this.user.authToken;
      this.user = await this.http.put<User>(this.loginUrl + this.user.userId, updatedInfo, {
        headers: new HttpHeaders({Authorization: this.authToken})
      }).toPromise();
      if (this.user.authToken == null) {
        this.user.authToken = this.authToken;
        localStorage.setItem('user', JSON.stringify(this.user));
      }
    }
    return this.user;
  }

  async getStatements(): Promise<any>{
    this.user = this.getUser();
    const statements: any[] = new Array();
    const accountTypeMap = new Map([
      ['CHECKING', 'Chequing'],
      ['SAVINGS', 'Savings'],
      ['STUDENT', 'Student'],
      ['FIRST_CLASS_CHECKING', 'First Class Chequing'],
      ['PREMIUM_VISA', 'Premium Visa'],
      ['BUSINESS_VISA', 'Business Visa']
    ]);
    if (this.user.authToken){
      for (const item of this.user.ownedAccounts) {
        const accountId = item.accountId;
        const statementList = new StatementList();
        statementList.transactions = await this.http.get<[]>(this.statementsUrl + accountId, {
          headers: new HttpHeaders({Authorization: this.user.authToken})
        }).toPromise();
        statementList.accountType = accountTypeMap.get(item.accountType);
        const currentDate = new Date();
        statementList.transactions.forEach(statement => {
          const previousMonthStart = new Date(currentDate.setMonth(currentDate.getMonth() - 1));
          const previousMonthEnd = new Date(previousMonthStart.getFullYear(), previousMonthStart.getMonth() + 1, 0);
          statement.startDate = previousMonthStart;
          statement.endDate = previousMonthEnd;
          statement.accountType = statementList.accountType;
        });
        statements.push(statementList);

      }
    }
    return statements;
  }

  async createNewAccount(accountType: AccountType): Promise<void> {
    this.user = this.getUser();
    if (this.user.authToken) {

      const newAccount = new AccountCreateRequest();
      newAccount.accountType = accountType.toUpperCase();
      newAccount.balance = 0;
      newAccount.isMainAccount = false;
      newAccount.openedByCustomerId = this.user.userId;

      this.authToken = this.user.authToken;
      const response = await this.http.post(this.createNewAccountUrl, newAccount, {
        headers: new HttpHeaders({Authorization: this.user.authToken})
      }).toPromise();

      if ((response as Account).accountId) {
        this.user = await this.http.get<User>(this.loginUrl + this.user.userId, {
          headers: new HttpHeaders({Authorization: this.user.authToken})
        }).toPromise();
        this.user.authToken = this.authToken;
        /*forkJoin([of(this.user)]).subscribe(() => {
          localStorage.setItem('user', JSON.stringify(this.user));
          window.location.reload();
        });*/
        localStorage.setItem('user', JSON.stringify(this.user));
        window.location.reload();
      }
    }
  }

  register(customer: any): void {
    this.http.post(this.registerUrl, customer, {observe: 'response'}).subscribe((response: any) => {
      console.log(response);
      if (response.status === 201) {
        this.router.navigateByUrl('/login');
      }
    });
  }

  public initializeSessionTimeout(): void {
    AppService.runTimer = true;

    this.reset();
    this.initListener();
    this.initInterval();
  }

  get lastAction(): number {
    return parseInt(localStorage.getItem(STORE_KEY), 10);
  }

  set lastAction(value) {
    localStorage.setItem(STORE_KEY, value.toString());
  }

  private initListener(): void {
    this.zone.runOutsideAngular(() => {
      this.userActivityChangeCallback = ($event) => this.handleUserActiveState($event);
      window.document.addEventListener('click', this.userActivityChangeCallback.bind(this), true);
    });
  }

  handleUserActiveState(event): void {
    this.reset();
  }

  public reset(): void {
    this.lastAction = Date.now();
    if (this.userIdlenessChecker) {
      this.userIdlenessChecker.next('RESET_TIMER');
    }
  }

  private initInterval(): void {
    const intervalDuration = 1000;
    this.sessionForIdle = interval(intervalDuration).pipe(
      map((tick: number) => {
        return tick;
      }),
      takeWhile(() => AppService.runTimer)
    );

    this.check();
  }

  private check(): void {
    this.sessionForIdle
      .subscribe(() => {
        const now = Date.now();
        const timeleft = this.lastAction + this.USER_IDLE_TIMER_VALUE_IN_MIN * 60 * 1000;
        const diff = timeleft - now;
        const isTimeout = diff < 0;

        this.userIdlenessChecker.next(`${diff}`);

        if (isTimeout) {
          this.logout();
        }
      });
  }

  ngOnDestroy(): void {
    if (this.userIdlenessChecker) {
      this.userIdlenessChecker.unsubscribe();
      this.userIdlenessChecker = undefined;
    }
  }

}
