import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Connection } from 'src/app/model/Connection/connection.model';
import { ApiPaymybuddyService } from 'src/app/service/api-paymybuddy.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {

  connections: Connection[] = [];
  request: Connection = new Connection();
  pages: Array<number> = new Array<number>(0);
  currentPage: number = 0;
  page: number = 0;
  size: number = 3;
  error:string = "";
  requestForm:FormGroup= this.fb.group({
    "email": ["", [Validators.required, Validators.email]],
  });

  constructor(private api:ApiPaymybuddyService, private fb:FormBuilder) { }

  ngOnInit(): void {
    this.loadConnections();
  }

  loadConnections() {
    this.api.getPageOfConnections(this.page,this.size)
    .subscribe({
      next: (v) => {
        this.pages = new Array<number>(v.totalPages)
        this.connections = v.content;
      }
    });
  }

  doDeleteConnection(id:number) {
    this.api.deleteConnection(id)
    .subscribe({
      next: (v) => {
        this.loadConnections();
      }
    });
  }

  doRequest() {
    if (this.requestForm.invalid) {
      return;
    }
    this.api.addConnection(this.requestForm.value)
    .subscribe({
      next: (v) => {
        this.loadConnections();
      },
      error: (e) => {
        if (e.status == 404 || e.status == 409) {
          this.error = e.error;
        } else {
          this.error = "An error occured, please try again."
        }
      }
    });
  }

  check(form:FormGroup,controleName:string,error:string):boolean {
    let control = form.controls[controleName];
    if (control.hasError(error) && (control.touched || control.dirty)) {
      return true
    }
    return false;
  }

  onPage(i: number) {
    this.currentPage = i;
    this.loadConnections();
  }

  onPrec() {
    if (this.currentPage > 0) {
      this.onPage(this.currentPage - 1);
    }
  }

  onNext() {
    if (this.currentPage < this.pages.length-1) {
      this.onPage(this.currentPage + 1);
    }
  }

}
