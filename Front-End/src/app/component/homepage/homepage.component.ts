import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BankAccount } from 'src/app/model/BankAccount/bank-account.model';
import { BankTransfer } from 'src/app/model/BankTransfer/bank-transfer.model';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/api-paymybuddy.service';
declare var bootstrap: any;

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  user: User = new User();
  bankaccounts:BankAccount[] = [];
  bankTransfers:BankTransfer[] = [];
  error:string = "";
  isIncome:boolean = true;
  bankTransferForm:FormGroup = this.fb.group({
    "bankAccountId": [null],
    "title": [{value: '', disabled: true}, Validators.required],
    "iban": [{value: '', disabled: true}, Validators.required],
    "bic": [{value: '', disabled: true}, Validators.required],
    "amount": []
  });
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 3;

  constructor(private api: ApiPaymybuddyService, private fb:FormBuilder) { }

  ngOnInit(): void {
    this.loadUser();
    this.loadBankAccounts();
    this.loadBankTransfers();
    console.log(this.bankaccounts)
  }

  loadUser() {
    this.api.getUser()
      .subscribe({
        next: (v) => {
          this.user = v;
        }
      });
  }

  loadBankTransfers() {
    this.api.getPageOfBankTransfers(this.currentPage,this.size)
    .subscribe({
      next: (v) => {
        this.pages = new Array<number>(v.totalPages)
        this.bankTransfers = v.content;
      }
    });
  }

  loadBankAccounts() {
    this.api.getAllBankAccounts()
      .subscribe({
        next: (v) => {
          console.log(v)
          this.bankaccounts = v;
        }
      });
  }

  requestTransfer() {
    let request:BankTransfer = <BankTransfer>this.bankTransferForm.value;
    request.isIncome = this.isIncome;
    this.api.requestBankTransfer(this.bankTransferForm.value)
    .subscribe({
      next: (v) => {
        this.loadUser();
        this.loadBankTransfers();
        var myModalEl = document.getElementById('bankTransferModal')
        var modal = bootstrap.Modal.getInstance(myModalEl) 
        modal.hide();
      },
      error: (e) => {
        if (e.status == 404 || e.status == 409) {
          this.error = e.error;
        } else {
          this.error = "An error occured, please try again."
        }
      }
    });
  }

  reverse() {
    this.isIncome = !this.isIncome;
  }

  select() {
    let controls = this.bankTransferForm.controls;
    if (controls['bankAccountId'].value == null ) {
      controls['title'].enable();
      controls['iban'].enable();
      controls['bic'].enable();
      controls['title'].reset();
      controls['iban'].reset();
      controls['bic'].reset();
      return;
    }
    controls['title'].disable();
    controls['iban'].disable();
    controls['bic'].disable();
    let id = this.bankTransferForm.controls['bankAccountId'].value;
    let bankAccount:BankAccount = this.bankaccounts.find(x => x.bankAccountId == id)!
    this.bankTransferForm.patchValue({
      title: bankAccount.title,
      iban : bankAccount.iban,
      bic : bankAccount.bic
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
