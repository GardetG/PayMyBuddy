import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/Authentication/authentication.service';
import { checkField } from 'src/app/Validator/checkField.utils';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  error:string="";
  form:FormGroup= this.fb.group({
    "email": ["", Validators.required],
    "password": ["", Validators.required],
    "remember": [false]
  });

  constructor(private auth: AuthenticationService, private router:Router, private fb:FormBuilder) { }

  ngOnInit(): void { }

  doLogin() {
    if (this.form.invalid) {
      Object.keys(this.form.controls).forEach(key => {
        this.form.controls[key].markAsTouched();
      });
      return;
    }
    console.log(this.form.value)
    this.auth.login(this.form.controls['email'].value, this.form.controls['password'].value, this.form.controls['remember'].value)
      .subscribe({
        next: (v) => {
          this.router.navigate(["home"]);
        },
        error: (e) => {
          if (e.status == 401) {
            this.error = "Wrong email or password, please try again.";
            this.form.reset();
          } else {
            this.error = "An error occured, please try again."
          }
        }
      });
  }

  check(form:FormGroup, controleName:string):boolean {
    return checkField(form,controleName,'required');
  }

}
