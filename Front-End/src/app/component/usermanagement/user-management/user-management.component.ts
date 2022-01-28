import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/ApiPayMyBuddy/api-paymybuddy.service';
import { AuthenticationService } from 'src/app/service/Authentication/authentication.service';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {

  error:string = "";
  users:User[] = [];
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 5;

  constructor(private api:ApiPaymybuddyService, private auth:AuthenticationService) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.api.getPageUsers(this.currentPage,this.size)
    .subscribe({
      next: (v) => {
        this.pages = new Array<number>(v.totalPages)
        this.users = v.content;
      }
    });
  }

  deleteUser(id:number) {
    var result = confirm("Are you sure to delete this user? This action is irreversible.");
    if (result) {
      this.api.deleteUser()
        .subscribe({
          next: (v) => {
            this.loadUsers();
          },
          error: (e) => {
            if (e.status == 409) {
              this.error = e.error;
            }
          }
        });
    }
  }

toggleEnable(id:number, enable:boolean) {
  this.auth.setEnabling(id, !enable)
  .subscribe({
    next: (v) => {
      console.log(v)
    },
    error: (e) => {
      console.log(e)
    }
  });
}

  onPage(i: number) {
    this.currentPage = i;
    this.loadUsers();
  }

  onPrec() {
    if (this.currentPage > 0) {
      this.onPage(this.currentPage - 1);
    }
  }

  onNext() {
    if (this.currentPage < this.pages.length-1) {
      this.onPage(this.currentPage + 1) ;
    }
  }

}
