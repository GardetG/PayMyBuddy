import { ComponentFactoryResolver, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../service/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private auth:AuthenticationService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

      console.log('oncheck')
      console.log(this.auth.getIdentity());
      if (this.auth.getIdentity().userId != 0) {
        // Identity has been set when login, so the user can pass to the route
        return true
      } else  {
        // Identity not set, route the user to the Login page
          this.router.navigate( ["/login"] );
          return false
      }
  }
}
