import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/User/user.model';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class ApiPaymybuddyService {

  constructor(private http:HttpClient, private auth:AuthenticationService) { }

  public getUser(): Observable<User> {
    return this.http.get<User>("http://localhost:8080/users/" + this.auth.identity.userId)
  }
}
