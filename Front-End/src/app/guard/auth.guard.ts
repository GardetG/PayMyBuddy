import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { Identity } from '../model/Identity/identity.model';
import { AuthenticationService } from '../service/Authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private auth:AuthenticationService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

      let identity:Identity = this.auth.getIdentity();
      if (identity.userId !=0) {
         // Identity set, user authenticate
        if (route.data['role'] && route.data['role'] != identity.role) {
          // User role note authorize
          switch(identity.role) {
            case "USER" : {
              this.router.navigate(['/home']);
              break;
            }
            case "ADMIN" : {
              this.router.navigate(['/home/admin']);
              break;
            }
            default : {
              this.router.navigate(['/login']);
              break;
            }
          }
          return false;
        }
        // user can pass
        return true;
      }
      // User not authenticate, return to login page
      this.router.navigate(['/login']);
      return false;
  }
}
