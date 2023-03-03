import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ShortenerComponent } from './shortener/shortener.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { LinkService } from './link.service';
import { SignupComponent } from './signup/signup.component';
import { LoginComponent } from './login/login.component';
import { AuthService } from './auth.service';
import { RedirectComponent } from './redirect/redirect.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { MyurlsComponent } from './myurls/myurls.component';
import { MyurlComponent } from './myurl/myurl.component';

@NgModule({
  declarations: [
    AppComponent,
    ShortenerComponent,
    NavbarComponent,
    SignupComponent,
    LoginComponent,
    RedirectComponent,
    NotFoundComponent,
    MyurlsComponent,
    MyurlComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [LinkService, AuthService],
  bootstrap: [AppComponent]
})
export class AppModule { }
