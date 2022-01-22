import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BankAccount } from 'src/app/model/BankAccount/bank-account.model';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/api-paymybuddy.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: User = new User();
  bankAccounts: BankAccount[] = [];
  isEditing: boolean = false;
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 3;
  error:string="";
  bankAccountForm:FormGroup = this.fb.group({
    "title": ["", Validators.required],
    "iban": ["", Validators.required],
    "bic": ["", Validators.required],
  });

  constructor(private api: ApiPaymybuddyService, private fb:FormBuilder) { }

  ngOnInit(): void {
    this.loadUser();
    this.loadBankAccounts();
  }

  loadUser() {
    this.api.getUser()
      .subscribe({
        next: (v) => {
          console.log(v)
          this.user = v;
        }
      });
  }

  loadBankAccounts() {
    this.api.getPageOfBankAccounts(this.currentPage,this.size)
    .subscribe({
      next: (v) => {
        this.pages = new Array<number>(v.totalPages)
        this.bankAccounts = v.content;
      }
    });
  }

  deleteAccount(id:number) {
    this.api.deleteBankAccount(id)
    .subscribe({
      next: (v) => {
        this.loadBankAccounts();
      }
    });
  }

  addAccount() {
    this.api.addBankAccount(this.bankAccountForm.value)
    .subscribe({
      next: (v) => {
        this.loadBankAccounts();
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

  toggleEdit() {
    this.isEditing = !this.isEditing;
  }
  onPage(i: number) {
    this.currentPage = i;
    this.loadBankAccounts();
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
