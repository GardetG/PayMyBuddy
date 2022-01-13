import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  error:string="";
  form:FormGroup= this.fb.group({
    "email": ["", Validators.required],
    "password": ["", Validators.required]
  });

  constructor(private auth: AuthenticationService, private router:Router, private fb:FormBuilder) { }

  ngOnInit(): void {
  }

  get email() {
    return this.form.get('email');
  }

  get password() {
    return this.form.get('password');
  }

  doLogin() {
    this.auth.login(this.email?.value, this.password?.value)
      .subscribe({
        next: (v) => {
          this.router.navigate(["home"]);
        },
        error: (e) => {
          this.error = "Wrong email or password, please try again.";
          this.form.reset();
        }
      });
  }
}
