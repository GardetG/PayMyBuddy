import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { MustMatch } from 'src/app/Validator/confirmpassword.validator';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  error:string="";
  form:FormGroup= this.fb.group({
    "firstname": ["", Validators.required],
    "lastname": ["", Validators.required],
    "email": ["", Validators.required],
    "password": ["", [Validators.required, Validators.minLength(8)]],
    "confirmpassword": [""],
    "termsAgreement": [false, Validators.requiredTrue],
    "privacyAgreement": [false, Validators.requiredTrue],
  }, {
    validator: MustMatch('password', 'confirmpassword')
  });

  constructor(private auth:AuthenticationService,private router:Router,private fb:FormBuilder) { }

  ngOnInit(): void {
  }

  get getControls() {
    return this.form.controls;
}

  get firstname() {
    return this.form.get('firstname');
  }

  get lastname() {
    return this.form.get('lastname');
  }

  get email() {
    return this.form.get('email');
  }

  get password() {
    return this.form.get('password');
  }

  get confirmpassword() {
    return this.form.get('confirmpassword');
  }

  doRegister() {}

}
