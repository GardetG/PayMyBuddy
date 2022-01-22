import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Connection } from '../model/Connection/connection.model';
import { User } from '../model/User/user.model';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class ApiPaymybuddyService {

  userId:number = 0;

  constructor(private http:HttpClient, private auth:AuthenticationService) {
    this.userId = auth.identity.userId
   }

  public getUser(): Observable<User> {
    return this.http.get<User>("http://localhost:8080/users/" + this.userId)
  }

  // Connection end-points
  public getAllConnections(): Observable<Connection[]> {
    return this.http.get<any>("http://localhost:8080/users/" + this.userId + "/connections")
    .pipe(map(page => {
      return page.content;
    }));
  }

  public getPageOfConnections(page:number, size:number): Observable<any> {
    let pageable = "?size=" + page + "&size=" + size
    return this.http.get<any>("http://localhost:8080/users/" + this.userId + "/connections" + pageable)
  }

  public deleteConnection(id:number): Observable<any> {
    return this.http.delete("http://localhost:8080/users/" + this.userId + "/connections/" + id)
  }

  public addConnection(request:Connection): Observable<Connection> {
    return this.http.post<Connection>("http://localhost:8080/users/" + this.userId + "/connections/", request)
  }
}
