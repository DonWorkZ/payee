import {Transaction} from './transaction.model';

export class Account {
  openedByCustomerId: number;
  accountType: string;
  balance: number; // this is fundingAmount
  accountId: number;
  isMainAccount: boolean;
  allTransactions: Transaction[];
  interestRate: number;
  minBalance: number;
  minBalanceCharge: number;
  accountNumber: number;
}
