import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { AuthenticationService } from 'src/app/service/authentication.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private auth:AuthenticationService ,private activatedRoute: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.router.events.pipe
      (filter(value => value instanceof NavigationEnd)).subscribe((value: any) => {
          console.log(this.router.url) // ['home']
      });
  }

  doLogOff() {
    this.auth.logoff()
    .subscribe({
      next: (v) => {
        console.log(v);
        this.router.navigate(["login"]);
      },
      error: (e) => {
        console.log(e);
      }
    });
  }

}
