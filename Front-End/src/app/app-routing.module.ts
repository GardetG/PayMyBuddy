import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ContactComponent } from './component/contact/contact.component';
import { HomeComponent } from './component/home/home.component';
import { HomepageComponent } from './component/homepage/homepage.component';
import { LoginComponent } from './component/login/login.component';
import { ProfileComponent } from './component/profile/profile.component';
import { RegisterComponent } from './component/register/register.component';
import { TransferComponent } from './component/transfer/transfer.component';
import { AuthGuard } from './guard/auth.guard';

const routes: Routes = [
  {
    path:"",
    redirectTo:"login",
    pathMatch:"full"
  },
  {
    path:"login",
    component:LoginComponent
  },
  {
    path:"register",
    component:RegisterComponent
  },
  {
    path:"home",
    component:HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path:"",
        component:HomepageComponent,
        pathMatch:"full"
      },
      {
        path:"transfer",
        component:TransferComponent,
        data: { breadcrumbs: ['home', 'Transfer'] }
      },
      {
        path:"profile",
        component:ProfileComponent,
        data: { breadcrumbs: ['home', 'Profile'] }
      },
      {
        path:"contact",
        component:ContactComponent,
        data: { breadcrumbs: ['home', 'Contact'] }
      },
    ]
  },
  {
    path:"**",
    redirectTo:"login"
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
