import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BankAccount } from 'src/app/model/BankAccount/bank-account.model';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/api-paymybuddy.service';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { MustMatch } from 'src/app/Validator/confirmpassword.validator';
declare var bootstrap: any;

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: User = new User();
  bankAccounts: BankAccount[] = [];
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  size: number = 3;
  error: string = "";
  bankAccountForm: FormGroup = this.fb.group({
    "title": ["", Validators.required],
    "iban": ["", [Validators.required, Validators.minLength(14), Validators.maxLength(34)]],
    "bic": ["", [Validators.required, Validators.minLength(8), Validators.maxLength(11)]],
    "agreement": [false, Validators.requiredTrue],
  });
  userForm: FormGroup = this.fb.group({
    "firstname": ["", Validators.required],
    "lastname": ["", Validators.required],
    "email": ["", [Validators.required, Validators.email]],
    "password": [null, Validators.minLength(8)],
    "confirmPassword": [null]
  }, {
    validator: MustMatch('password', 'confirmPassword')
  });

  constructor(private api: ApiPaymybuddyService, private fb: FormBuilder, private router:Router,private auth:AuthenticationService) { }

  ngOnInit(): void {
    this.loadUser();
    this.loadBankAccounts();
  }

  loadUser() {
    this.api.getUser()
      .subscribe({
        next: (v) => {
          this.user = v;
          this.userForm.patchValue({
            firstname: this.user.firstname,
            lastname: this.user.lastname,
            email: this.user.email
          });
        }
      });
  }

  loadBankAccounts() {
    this.api.getPageOfBankAccounts(this.currentPage, this.size)
      .subscribe({
        next: (v) => {
          this.pages = new Array<number>(v.totalPages)
          this.bankAccounts = v.content;
        }
      });
  }

  editUser() {
    if (this.userForm.invalid) {
      console.log('ERROR !!!')
      return;
    }
    this.api.updateUser(this.userForm.value)
      .subscribe({
        next: (v) => {
          this.user = v;
          this.error = "";
          var myModalEl = document.getElementById('userModal')
          var modal = bootstrap.Modal.getInstance(myModalEl)
          modal.hide();
        },
        error: (e) => {
          this.error = e.error;
        }
      });
  }

  deleteUser() {
    var result = confirm("Are you sure to delete your profile? This action is irreversible and you won't be able to login back.")
    if (result) {
      this.api.deleteUser()
        .subscribe({
          next: (v) => {
            this.closeUserModal();
            this.doLogOff();
          },
          error: (e) => {
            if (e.status == 409) {
              this.error = e.error;
            }
          }
        });
    }
  }

  doLogOff() {
    this.auth.logoff()
    .subscribe({
      next: (v) => {
        this.router.navigate(["login"]);
      },
      error: (e) => {
        console.log(e);
      }
    });
  }

  deleteAccount(id: number) {
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

  closeBankAccountModal() {
    this.error=""
    this.bankAccountForm.reset();
    var myModalEl = document.getElementById('bankAccountModal');
    var modal = bootstrap.Modal.getInstance(myModalEl);
    modal.hide();
  }

  closeUserModal() {
    this.error="";
    this.bankAccountForm.reset();
    this.userForm.patchValue({
      firstname: this.user.firstname,
      lastname: this.user.lastname,
      email: this.user.email,
      password: null,
      confirmPassword: null
    });
    var myModalEl = document.getElementById('userModal');
    var modal = bootstrap.Modal.getInstance(myModalEl);
    modal.hide();
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
    if (this.currentPage < this.pages.length - 1) {
      this.onPage(this.currentPage + 1);
    }
  }

  check(form: FormGroup, controleName: string, error: string): boolean {
    let control = form.controls[controleName];
    if (control.hasError(error) && (control.touched || control.dirty)) {
      return true;
    }
    return false;
  }

}
