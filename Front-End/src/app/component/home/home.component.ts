import { Component, OnInit } from '@angular/core';
import { filter } from 'rxjs';
import { Breadcrumb } from 'src/app/model/Breadcrumb/breadcrumb.model';
import { AuthenticationService } from 'src/app/service/Authentication/authentication.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  menus: Breadcrumb[] = [];
  breadcrumbs: Breadcrumb[] = [];
  currentRoute: string = ""

  constructor(private auth: AuthenticationService, private router: Router) { }

  ngOnInit(): void {
    this.loadMenu(this.auth.getIdentity().role);
    this.parseRoute(this.router.url);
    this.router.events.pipe
      (filter(value => value instanceof NavigationEnd)).subscribe((value: any) => {
        this.parseRoute(this.router.url);
      });
  }

  parseRoute(url: string) {
    let elements: string[] = url.substring(1).split('/')

    this.breadcrumbs = elements.slice(0, elements.length - 1)
      .map(function callback(value, index, array) {
        let breadcrumb: Breadcrumb = new Breadcrumb();
        breadcrumb.label = value[0].toUpperCase() + value.slice(1);
        breadcrumb.url = './' + array.slice(1, index + 1).join("/");
        return breadcrumb;
      });
    let route: string = elements.pop()!;
    this.currentRoute = route[0].toUpperCase() + route.slice(1);
  }

  loadMenu(role: string) {
    switch (role) {
      case "USER": {
        this.menus = [
          {
            label: "Home",
            url: "./"
          },
          {
            label: "Transfer",
            url: "./transfer"
          },
          {
            label: "Profile",
            url: "./profile"
          },
          {
            label: "Contact",
            url: "./contact"
          }
        ];
        break;
      }
      case "ADMIN": {
        this.menus = [
          {
            label: "Admin",
            url: "./admin"
          },
          {
            label: "Users",
            url: "./admin/users"
          },
          {
            label: "Transactions",
            url: "./admin/transactions"
          },
          {
            label: "Bank Transfers",
            url: "./admin/banktransfers"
          },
        ];
        break;
      }
      default: { break; }
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

}
