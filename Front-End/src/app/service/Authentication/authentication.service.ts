import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Identity } from 'src/app/model/Identity/identity.model';
import { User } from 'src/app/model/User/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private identity: Identity = new Identity();
  private baseURL:string = "http://localhost:8080";

  constructor(private http:HttpClient) { }

  getIdentity() : Identity {
    if (this.identity.userId ==  0) {
      let storage = localStorage.getItem('identity');
      if (storage != null) {
        this.identity = JSON.parse(storage);
      }
    }
    return this.identity;
  }

  getBasseURL():string {
    return this.baseURL;
  }

  public login(email:string, password:string,remember:boolean): Observable<Identity> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + btoa(email+":"+password)
    });
    return this.http.get<Identity>(this.baseURL + "/login?remember="+remember, {headers})
    .pipe(map(resp => {
      this.identity = resp;
      localStorage.setItem('identity', JSON.stringify(this.identity));
      return resp;
    }))
  }

  public register(user:User): Observable<User> {
    return this.http.post<User>(this.baseURL + "/register",user)
  }

  public logoff() {
    const headers = new HttpHeaders({
      "X-Requested-With": "XMLHttpRequest"})
    return this.http.get<User>(this.baseURL + "/logout",{headers})
    .pipe(map(resp => {
      this.identity = new Identity();
      localStorage.clear();
      return resp
    }));
  }

}
