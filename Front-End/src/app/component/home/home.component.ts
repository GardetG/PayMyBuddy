import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { Breadcrumb } from 'src/app/model/Breadcrumb/breadcrumb.model';
import { AuthenticationService } from 'src/app/service/authentication.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  breadcrumbs:Breadcrumb[] = [];
  currentRoute:string=""

  constructor(private auth:AuthenticationService , private router: Router) { }

  ngOnInit(): void {
    this.parseRoute(this.router.url);
    this.router.events.pipe
      (filter(value => value instanceof NavigationEnd)).subscribe((value: any) => {
          this.parseRoute(this.router.url);
      });
  }

  parseRoute(url:string) {
    let elements:string[] = url.substring(1).split('/')
    .map(element => element[0].toUpperCase() + element.slice(1));

    this.breadcrumbs = elements.slice(0,elements.length-1)
    .map(function callback(value, index, array) {
      let breadcrumb:Breadcrumb = new Breadcrumb();
      breadcrumb.label = value;
      breadcrumb.url = './' + array.slice(1, index+1).join("/");
      return breadcrumb;
    });

    this.currentRoute = elements.pop()!;
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

}
