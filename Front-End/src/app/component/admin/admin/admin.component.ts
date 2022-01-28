import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/ApiPayMyBuddy/api-paymybuddy.service';
import { checkField } from 'src/app/Validator/checkField.utils';
import { MustMatch } from 'src/app/Validator/confirmpassword.validator';
declare var bootstrap: any;

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  user: User = new User();
  info:any = {};
  traces:any[] = [];
  error: string = "";
  changePwd:boolean = false;
  adminForm: FormGroup = this.fb.group({
    "firstname": ["", Validators.required],
    "lastname": ["", Validators.required],
    "email": ["", [Validators.required, Validators.email]],
    "password": [null],
    "confirmpassword": [null]
  }, {
    validator: MustMatch('password', 'confirmpassword')
  });

  constructor(private api: ApiPaymybuddyService, private fb:FormBuilder) { }

  ngOnInit(): void {
    this.loadUser();
    this.loadInfo();
    this.loadTraces();
  }

  loadUser() {
    this.api.getUser()
      .subscribe({
        next: (v) => {
          this.user = v;
          this.adminForm.patchValue({
            firstname: this.user.firstname,
            lastname: this.user.lastname,
            email: this.user.email
          });
        }
      });
  }

  loadInfo() {
    this.api.getInfo()
      .subscribe({
        next: (v) => {
          this.info = v;
        }
      });
  }

  loadTraces() {
    this.api.getTrace()
      .subscribe({
        next: (v) => {
          this.traces = v.traces;
        }
      });
  }

  editAdmin() {
    if (this.adminForm.invalid) {
      Object.keys(this.adminForm.controls).forEach(key => {
        this.adminForm.controls[key].markAsTouched();
      });
      return;
    }
    this.api.updateUser(this.adminForm.value)
      .subscribe({
        next: (v) => {
          this.user = v;
          this.closeAdminModal();
        },
        error: (e) => {
          if (e.status == 404 || e.status == 409) {
            this.error = e.error + ".";
          } else {
            this.error = "An error occured, please try again."
          }
        }
      });
  }

  togglePwd() {
    this.changePwd = !this.changePwd;
    if (this.changePwd) {
      this.adminForm.controls['password'].setValidators([Validators.required, Validators.minLength(8)]);   
     } else {
      this.adminForm.controls['password'].setValidators(null);   
     }
    this.adminForm.patchValue({  
      password: null,
      confirmpassword: null
    });
  }

  closeAdminModal() {
    this.error="";
    this.changePwd=false;
    this.adminForm.reset();
    this.adminForm.patchValue({
      firstname: this.user.firstname,
      lastname: this.user.lastname,
      email: this.user.email,
      password: null,
      confirmpassword: null
    });
    var myModalEl = document.getElementById('adminModal');
    var modal = bootstrap.Modal.getInstance(myModalEl);
    modal.hide();
  }

  check(form: FormGroup, controleName: string, error: string): boolean {
    return checkField(form, controleName, error);
  }

}
