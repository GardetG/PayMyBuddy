import { Component, OnInit } from '@angular/core';
import { Connection } from 'src/app/model/Connection/connection.model';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {

  connections: Connection[] = [];
  request: Connection = new Connection();
  pages: Array<number> = new Array<number>(4);
  currentPage: number = 0;
  page: number = 0;
  size: number = 3;

  constructor() { }

  ngOnInit(): void {

  }

  onRequest() { }

  doDeleteConnection(id:number) {
    
  }

  onPage(i: number) {
    this.currentPage = i;
  }

  onPrec() {
    if (this.currentPage > 0) {
      this.currentPage -= 1;
    }
  }

  onNext() {
    if (this.currentPage < this.pages.length-1) {
      this.currentPage += 1;
    }
  }

}
