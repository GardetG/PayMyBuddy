import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/api-paymybuddy.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  user!: User;

  constructor(private service:ApiPaymybuddyService) { 
    service.getUser()
    .subscribe({
      next: (v) => {
        this.user = v;
      }
    });
  }

  ngOnInit(): void {
  }

}
