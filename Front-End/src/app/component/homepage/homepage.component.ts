import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/User/user.model';
import { ApiPaymybuddyService } from 'src/app/service/api-paymybuddy.service';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  user: User = new User();

  constructor(private apiService: ApiPaymybuddyService) { }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser() {
    this.apiService.getUser()
      .subscribe({
        next: (v) => {
          this.user = v;
        }
      });
  }

}
