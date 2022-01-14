import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Identity } from '../model/Identity/identity.model';
import { User } from '../model/User/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  public identity!: Identity;

  constructor(private http:HttpClient) { }

  public login(email:string, password:string): Observable<Identity> {
    const headers = new HttpHeaders({
      "X-Requested-With": "XMLHttpRequest",
      Authorization: 'Basic ' + btoa(email+":"+password)
    });
    return this.http.get<Identity>("http://localhost:8080/login", {headers})
    .pipe(map(resp => {
      this.identity = resp;
      return resp;
    }))
  }

  public register(user:User): Observable<User> {
    return this.http.post<User>("http://localhost:8080/register",user)
  }

  public logoff() {
    const headers = new HttpHeaders({
      "X-Requested-With": "XMLHttpRequest"})
    return this.http.post<User>("http://localhost:8080/logout",{headers})
    .pipe(map(resp => {
      this.identity = new Identity();
      return resp
    }));
  }

}
