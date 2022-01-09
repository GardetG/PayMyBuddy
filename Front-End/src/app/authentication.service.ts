import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(private http:HttpClient) { }

  public login(email:string, password:string) {
    const headers = new HttpHeaders({Authorization: 'Basic ' + btoa(email+":"+password)});
    return this.http.get("http://localhost:8080/login", {headers, responseType:'text' as 'json'});
  }
}
