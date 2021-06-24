import {Transaction} from './transaction.model';

export class Statement {
  transactions: Transaction[];
  accountType: string;
  startDate: Date;
  endDate: Date;
}
