import {Address} from './address.model';
import {Account} from './account.model';

export class User {
   username: string; // this is clientNumber
   password: string;
   userId: number;
   title: string;
   firstName: string;
   lastName: string;
   email: string;
   phoneNumber: string;
   industry: string;
   occupation: string;
   isActive: boolean;
   income: string;
   ownedAccounts: Account[];
   mainAccount: Account;
   role: string;
   authToken: string;
   addressList: Address[];
}
