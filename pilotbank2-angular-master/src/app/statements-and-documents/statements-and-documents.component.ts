import {Component, OnInit} from '@angular/core';
import {AppService} from '../services/app.service';
import {StatementList} from '../models/statementList.model';
import {Statement} from '../models/statement.model';
import {PageEvent} from '@angular/material/paginator';

@Component({
  selector: 'app-statements-and-documents',
  templateUrl: './statements-and-documents.component.html',
  styleUrls: ['./statements-and-documents.component.css']
})
export class StatementsAndDocumentsComponent implements OnInit {

  statements: StatementList[];
  allStatements: Statement[] = new Array<Statement>();
  filteredStatements: Statement[] = new Array<Statement>();
  filteredPaginatedStatements: Statement[];
  currentPage: number;
  statementsPerPage: number;
  indexStart: number;
  indexEnd: number;
  selectedFilter: string;
  month: string;
  year: number;
  account: number;
  accountType: string;

  filters = ['None', 'Date', 'Account', 'Account Type'];

  months = new Set();
  years = new Set();
  accounts = new Map();
  accountTypes = new Set();

  constructor(private appService: AppService) {
  }

  async ngOnInit(): Promise<void> {
    this.statements = await this.appService.getStatements();
    this.filteredPaginatedStatements = new Array<Statement>();
    this.currentPage = 0;
    this.statementsPerPage = 5;
    for ( let i = 0; i < this.statements.length; i++) {
      for (const statement of this.statements[i].transactions) {
        this.allStatements.push(statement);
        this.months.add(statement.startDate.toLocaleDateString('default', {month: 'long'}));
        this.years.add(statement.startDate.getFullYear());
        this.accountTypes.add(statement.accountType);
      }
      this.accounts.set(i, this.statements[i].accountType);
    }
    this.filteredStatements = this.allStatements;

    if (this.statementsPerPage <= this.filteredStatements.length) {
      this.indexStart = 1;
      this.indexEnd = 5;
      this.filteredPaginatedStatements = this.filteredStatements.slice(
        this.currentPage * this.statementsPerPage, this.currentPage * this.statementsPerPage + this.statementsPerPage
      );
    }
    else{
      this.indexStart = 1;
      this.indexEnd = this.filteredStatements.length;
      for (const statement of this.filteredStatements) {
        this.filteredPaginatedStatements.push(statement);
      }
    }

  }

  setSelectedFilter(): void {

  }

  filter(): void {
    this.filteredStatements = new Array<Statement>();
    if (this.selectedFilter === 'None'){
      this.month = null;
      this.year = null;
      this.account = null;
      this.accountType = null;
    }
    if (this.month && this.year && this.selectedFilter === 'Date'){
      for (const statement of this.allStatements) {
        if (this.month === statement.startDate.toLocaleDateString('default', {month: 'long'}) &&
            this.year.toString() === statement.startDate.getFullYear().toString()){
          this.filteredStatements.push(statement);
        }
      }
    }
    else if (this.month && this.selectedFilter === 'Date'){
      for (const statement of this.allStatements) {
        if (this.month === statement.startDate.toLocaleDateString('default', {month: 'long'})){
          this.filteredStatements.push(statement);
        }
      }
    }
    else if (this.year && this.selectedFilter === 'Date'){
      for (const statement of this.allStatements) {
        if (this.year.toString() === statement.startDate.getFullYear().toString()){
          this.filteredStatements.push(statement);
        }
      }
    }
    else if (this.accountType && this.selectedFilter === 'Account Type'){
      for (const statement of this.allStatements) {
        if (this.accountType === statement.accountType){
          this.filteredStatements.push(statement);
        }
      }
    }
    else if (this.account && this.selectedFilter === 'Account'){
      for (const statement of this.statements[Number(this.account)].transactions) {
        this.filteredStatements.push(statement);
      }
      this.filteredStatements = this.filteredStatements.slice();
    }
    else{
      for (const statement of this.allStatements) {
        this.filteredStatements.push(statement);
      }
    }
    this.paginate();
  }

  paginate(): void {
    this.currentPage = 0;
    this.indexStart = 1;
    if (this.statementsPerPage <= this.filteredStatements.length) {
      this.indexEnd = this.statementsPerPage;
      this.filteredPaginatedStatements = this.filteredStatements.slice(
        this.currentPage * this.statementsPerPage, this.currentPage * this.statementsPerPage + this.statementsPerPage
      );
    }
    else{
      this.indexEnd = this.filteredStatements.length;

      this.filteredPaginatedStatements = this.filteredStatements.slice(
          0, this.filteredStatements.length
      );
    }
  }

  previousPage(): void {

    if (this.currentPage > 0){
      this.currentPage = this.currentPage - 1;
      this.indexStart = this.currentPage * this.statementsPerPage + 1;
      this.indexEnd = this.currentPage * this.statementsPerPage + this.statementsPerPage;

      this.filteredPaginatedStatements = this.filteredStatements.slice(
        this.currentPage * this.statementsPerPage, this.currentPage * this.statementsPerPage + this.statementsPerPage
      );
    }

  }

  nextPage(): void {


    if (this.indexEnd + this.statementsPerPage > this.filteredStatements.length){
      this.currentPage = Math.floor(this.filteredStatements.length / this.statementsPerPage);

      if (this.indexStart + this.statementsPerPage < this.filteredStatements.length) {
        this.indexStart = this.indexStart + this.statementsPerPage;
      }

      this.indexEnd = this.filteredStatements.length;

      this.filteredPaginatedStatements = this.filteredStatements.slice(
        this.currentPage * this.statementsPerPage, this.currentPage * this.statementsPerPage + this.statementsPerPage
      );

    }
    else{
      this.currentPage = this.currentPage + 1;
      this.indexStart = this.indexStart + this.statementsPerPage;
      this.indexEnd = this.indexEnd + this.statementsPerPage;

      this.filteredPaginatedStatements = this.filteredStatements.slice(
        this.currentPage * this.statementsPerPage, this.currentPage * this.statementsPerPage + this.statementsPerPage
      );
    }
  }

}
