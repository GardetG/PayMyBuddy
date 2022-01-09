import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/service/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  email:string = "";
  password:string = "";
  error:string="";

  constructor(private service: AuthenticationService, private router:Router) { }

  ngOnInit(): void {
  }

  doLogin() {
    this.service.login(this.email, this.password)
      .subscribe({
        next: (v) => {
          this.router.navigate(["home"]);
        },
        error: (e) => {
          this.error = "Email ou mot de passe incorrect."
        }
      });

  }

}
