import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BankAccount } from 'src/app/model/BankAccount/bank-account.model';
import { BankTransfer } from 'src/app/model/BankTransfer/bank-transfer.model';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/ApiPayMyBuddy/api-paymybuddy.service';
import { checkField } from 'src/app/Validator/checkField.utils';
declare var bootstrap: any;

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  error:string = "";
  user: User = new User();
  bankaccounts:BankAccount[] = [];
  bankTransfers:BankTransfer[] = [];
  bankTransferForm:FormGroup = this.fb.group({
    "isIncome": [true, {initialValueIsDefault: true}],
    "bankAccountId": [null, Validators.required],
    "iban": [{value: null, disabled: true}, Validators.required],
    "bic": [{value: null, disabled: true}, Validators.required],
    "amount": [null, [Validators.required, Validators.min(1), Validators.max(999.99)]],
    "agreement": [false, Validators.requiredTrue]
  });
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 3;

  constructor(private api: ApiPaymybuddyService, private fb:FormBuilder) { }

  ngOnInit(): void {
    this.loadUser();
    this.loadBankAccounts();
    this.loadBankTransfers();
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
          this.bankaccounts = v;
        }
      });
  }

  requestTransfer() {
    this.api.requestBankTransfer(this.bankTransferForm.value)
    .subscribe({
      next: (v) => {
        this.loadUser();
        this.loadBankTransfers();
        this.closeModal();
      },
      error: (e) => {
        if (e.status == 404 || e.status == 409) {
          this.error = e.error + ".";
        } else {
          this.error = "An error occured, please try again.";
        }
      }
    });
  }

  closeModal() {
    this.error = "";
    this.bankTransferForm.reset({isIncome: true});
    var myModalEl = document.getElementById('bankTransferModal')
    var modal = bootstrap.Modal.getInstance(myModalEl)
    modal.hide();
  }

  get isIncome() {
    return this.bankTransferForm.controls['isIncome'].value;
  }

  reverse() {
    this.bankTransferForm.patchValue({isIncome: !this.isIncome})
  }

  select() {
    let id = this.bankTransferForm.controls['bankAccountId'].value;
    let bankAccount:BankAccount = this.bankaccounts.find(x => x.bankAccountId == id)!
    this.bankTransferForm.patchValue({
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

  check(form: FormGroup, controleName: string, error: string): boolean {
    return checkField(form, controleName, error);
  }
}
