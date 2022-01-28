import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './component/login/login.component';
import { HomeComponent } from './component/home/home.component';
import { RegisterComponent } from './component/register/register.component';
import { TransferComponent } from './component/transfer/transfer.component';
import { ProfileComponent } from './component/profile/profile.component';
import { ContactComponent } from './component/contact/contact.component';
import { HomepageComponent } from './component/homepage/homepage.component';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { AuthenticationInterceptor } from './Interceptor/authentication.interceptor';
import { AuthenticationService } from './service/Authentication/authentication.service';
import { AdminComponent } from './component/admin/admin/admin.component';
import { UserManagementComponent } from './component/usermanagement/user-management/user-management.component';
import { TransactionsManagementComponent } from './component/transactions-management/transactions-management.component';
import { BankTransferManagementComponent } from './component/bank-transfer-management/bank-transfer-management.component';
registerLocaleData(localeFr, 'fr');

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    RegisterComponent,
    TransferComponent,
    ProfileComponent,
    ContactComponent,
    HomepageComponent,
    AdminComponent,
    UserManagementComponent,
    TransactionsManagementComponent,
    BankTransferManagementComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    AuthenticationService,
    [
      { provide: HTTP_INTERCEPTORS, useClass: AuthenticationInterceptor, multi: true }
    ]
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
