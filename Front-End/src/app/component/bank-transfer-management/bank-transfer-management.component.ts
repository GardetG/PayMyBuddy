import { Component, OnInit } from '@angular/core';
import { BankTransfer } from 'src/app/model/BankTransfer/bank-transfer.model';
import { ApiPaymybuddyService } from 'src/app/service/ApiPayMyBuddy/api-paymybuddy.service';

@Component({
  selector: 'app-bank-transfer-management',
  templateUrl: './bank-transfer-management.component.html',
  styleUrls: ['./bank-transfer-management.component.css']
})
export class BankTransferManagementComponent implements OnInit {

  bankTransfers:BankTransfer[] = [];
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 5;

  constructor(private api:ApiPaymybuddyService) { }

  ngOnInit(): void {
    this.loadBankTransfers();
  }

  loadBankTransfers() {
    this.api.getPageOfAllBankTransfers(this.currentPage,this.size)
    .subscribe({
      next: (v) => {
        this.pages = new Array<number>(v.totalPages)
        this.bankTransfers = v.content;
      }
    });
  }

  onPage(i: number) {
    this.currentPage = i;
    this.loadBankTransfers();
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
