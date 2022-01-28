import { Component, OnInit } from '@angular/core';
import { Transaction } from 'src/app/model/Transaction/transaction.model';
import { ApiPaymybuddyService } from 'src/app/service/ApiPayMyBuddy/api-paymybuddy.service';

@Component({
  selector: 'app-transactions-management',
  templateUrl: './transactions-management.component.html',
  styleUrls: ['./transactions-management.component.css']
})
export class TransactionsManagementComponent implements OnInit {

  transactions:Transaction[] = [];
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 5;

  constructor(private api:ApiPaymybuddyService) { }

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions() {
    this.api.getPageOfAllTransactions(this.currentPage,this.size)
    .subscribe({
      next: (v) => {
        this.pages = new Array<number>(v.totalPages)
        this.transactions = v.content;
      }
    });
  }

  onPage(i: number) {
    this.currentPage = i;
    this.loadTransactions();
  }

  onPrec() {
    if (this.currentPage > 0) {
      this.onPage(this.currentPage - 1);
    }
  }

  onNext() {
    if (this.currentPage < this.pages.length-1) {
      this.onPage(this.currentPage + 1);
    }
  }

}
