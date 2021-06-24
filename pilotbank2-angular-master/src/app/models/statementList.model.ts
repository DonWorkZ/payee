import {Statement} from './statement.model';

export class StatementList {
  transactions: Statement[]; // this field is actually an array of statements
  accountType: string;
}
