import {Address} from './address.model';
import {Account} from './account.model';
import {Role} from '../enums/role.enum';

export class Customer {
  role: Role;
  account: Account;
  securityQuestion: string;
  securityAnswer: string;
  userId: number;
  title: string;
  username: string; // this is clientNumber
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  sin: string;
  address: Address;
  industry: string;
  occupation: string;
  isActive: boolean;
  income: number;
}
