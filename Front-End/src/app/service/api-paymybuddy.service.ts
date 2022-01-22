import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { BankAccount } from '../model/BankAccount/bank-account.model';
import { BankTransfer } from '../model/BankTransfer/bank-transfer.model';
import { Connection } from '../model/Connection/connection.model';
import { Transaction } from '../model/Transaction/transaction.model';
import { User } from '../model/User/user.model';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class ApiPaymybuddyService {

  constructor(private http:HttpClient, private auth:AuthenticationService) {}

  public getUser(): Observable<User> {
    return this.http.get<User>("http://localhost:8080/users/" + this.auth.getIdentity().userId)
  }

  // Connection end-points

  public getAllConnections(): Observable<Connection[]> {
    return this.http.get<any>("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/connections")
    .pipe(map(page => {
      return page.content;
    }));
  }

  public getPageOfConnections(page:number, size:number): Observable<any> {
    let pageable = "?page=" + page + "&size=" + size
    return this.http.get<any>("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/connections" + pageable)
  }

  public addConnection(request:Connection): Observable<Connection> {
    return this.http.post<Connection>("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/connections/", request)
  }

   public deleteConnection(id:number): Observable<any> {
    return this.http.delete("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/connections/" + id)
  }

  // Bank account end-points

  public getAllBankAccounts(): Observable<BankAccount[]> {
    return this.http.get<any>("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/bankaccounts")
    .pipe(map(page => {
      return page.content;
    }));
  }

  public getPageOfBankAccounts(page:number, size:number): Observable<any> {
    let pageable = "?page=" + page + "&size=" + size
    return this.http.get<any>("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/bankaccounts" + pageable)
  }

  public addBankAccount(request:BankAccount): Observable<BankAccount> {
    return this.http.post<BankAccount>("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/bankaccounts/",request)
  }

  public deleteBankAccount(id:number): Observable<any> {
    return this.http.delete("http://localhost:8080/users/" + this.auth.getIdentity().userId + "/bankaccounts/" + id)
  }

  // Bank transfer end-points

  public getPageOfBankTransfers(page:number, size:number): Observable<any> {
    let pageable = "&page=" + page + "&size=" + size + "&sort=date,desc"
    return this.http.get<any>("http://localhost:8080/banktransfers/user?id=" + this.auth.getIdentity().userId + pageable)
  }

  public requestBankTransfer(request:BankTransfer): Observable<BankTransfer> {
    request.userId = this.auth.getIdentity().userId;
    return this.http.post<BankTransfer>("http://localhost:8080/banktransfers", request)
  }

    // Transaction end-points

    public getPageOfTransactions(page:number, size:number): Observable<any> {
      let pageable = "&page=" + page + "&size=" + size + "&sort=date,desc"
      return this.http.get<any>("http://localhost:8080/transactions/user?id=" + this.auth.getIdentity().userId + pageable)
    }
  
    public requestTransaction(request:Transaction): Observable<Transaction> {
      request.emitterId = this.auth.getIdentity().userId;
      return this.http.post<Transaction>("http://localhost:8080/transactions", request)
    }
}
