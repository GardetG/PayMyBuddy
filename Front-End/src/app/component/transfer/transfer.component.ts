import { Component, OnInit } from '@angular/core';
import { Connection } from 'src/app/model/Connection/connection.model';
import { Transaction } from 'src/app/model/Transaction/transaction.model';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {

  connections: Connection[] = [];
  transactions: Transaction[] = [];
  request: Connection = new Connection();
  pages: Array<number> = new Array<number>(4);
  currentPage: number = 0;
  page: number = 0;
  size: number = 3;

  error: string = "";

  constructor() { }

  ngOnInit(): void {
    console.log(this.connections);
  }

  onRequest() { }

  onPage(i: number) {
    this.currentPage = i;
  }

  onPrec() {
    if (this.currentPage > 0) {
      this.currentPage -= 1;
    }
  }

  onNext() {
    if (this.currentPage < this.pages.length-1) {
      this.currentPage += 1;
    }
  }
}