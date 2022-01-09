import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Identity } from '../model/Identity/identity.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  public identity!: Identity;

  constructor(private http:HttpClient) { }

  public login(email:string, password:string): Observable<Identity> {
    const headers = new HttpHeaders({Authorization: 'Basic ' + btoa(email+":"+password)});
    return this.http.get<Identity>("http://localhost:8080/login", {headers})
    .pipe(map(resp => {
      this.identity = resp;
      return resp;
    }))
  }
}
