import { Component, OnInit } from '@angular/core';
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
  pages: Array<number> = new Array<number>(4);
  currentPage: number = 0;
  page: number = 0;
  size: number = 3;

  constructor(private apiService: ApiPaymybuddyService) { }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser() {
    this.apiService.getUser()
      .subscribe({
        next: (v) => {
          this.user = v;
        }
      });
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
  }

  doDeleteAccount(id:number) {}

  onPage(i: number) {
    this.currentPage = i;
  }

  onPrec() {
    if (this.currentPage > 0) {
      this.currentPage -= 1;
    }
  }

  onNext() {
    if (this.currentPage < this.pages.length - 1) {
      this.currentPage += 1;
    }
  }

}
