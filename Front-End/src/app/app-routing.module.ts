import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './component/admin/admin/admin.component';
import { BankTransferManagementComponent } from './component/bank-transfer-management/bank-transfer-management.component';
import { ContactComponent } from './component/contact/contact.component';
import { HomeComponent } from './component/home/home.component';
import { HomepageComponent } from './component/homepage/homepage.component';
import { LoginComponent } from './component/login/login.component';
import { ProfileComponent } from './component/profile/profile.component';
import { RegisterComponent } from './component/register/register.component';
import { TransactionsManagementComponent } from './component/transactions-management/transactions-management.component';
import { TransferComponent } from './component/transfer/transfer.component';
import { UserManagementComponent } from './component/usermanagement/user-management/user-management.component';
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
        pathMatch:"full",
        canActivate: [AuthGuard],
        data: {role: 'USER'}
      },
      {
        path:"transfer",
        component:TransferComponent,
        canActivate: [AuthGuard],
        data: {role: 'USER'}
      },
      {
        path:"profile",
        component:ProfileComponent,
        canActivate: [AuthGuard],
        data: {role: 'USER'}
      },
      {
        path:"contact",
        component:ContactComponent,
        canActivate: [AuthGuard],
        data: {role: 'USER'}
      },
      {
        path:"admin",
        component:AdminComponent,
        canActivate: [AuthGuard],
        data: {role: 'ADMIN'},
      },
      {
        path:"admin/users",
        component:UserManagementComponent,
        canActivate: [AuthGuard],
        data: {role: 'ADMIN'},
      },
      {
        path:"admin/transactions",
        component:TransactionsManagementComponent,
        canActivate: [AuthGuard],
        data: {role: 'ADMIN'},
      },
      {
        path:"admin/banktransfers",
        component:BankTransferManagementComponent,
        canActivate: [AuthGuard],
        data: {role: 'ADMIN'},
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
