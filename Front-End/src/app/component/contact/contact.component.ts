import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Connection } from 'src/app/model/Connection/connection.model';
import { ApiPaymybuddyService } from 'src/app/service/ApiPayMyBuddy/api-paymybuddy.service';
import { checkField } from 'src/app/Validator/checkField.utils';

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
    this.api.getPageOfConnections(this.currentPage,this.size)
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
      Object.keys(this.requestForm.controls).forEach(key => {
        this.requestForm.controls[key].markAsTouched();
      });
      return;
    }
    this.api.addConnection(this.requestForm.value)
    .subscribe({
      next: (v) => {
        this.loadConnections();
        this.error=""
      },
      error: (e) => {
        if (e.status == 404 || e.status == 409) {
          this.error = e.error + ".";
        } else {
          this.error = "An error occured, please try again."
        }
      }
    });
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

  check(form:FormGroup,controleName:string,error:string):boolean {
    return checkField(form, controleName, error);
  }

}
