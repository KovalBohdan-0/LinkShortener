import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MyurlsComponent } from './myurls/myurls.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { RedirectComponent } from './redirect/redirect.component';
import { ShortenerComponent } from './shortener/shortener.component';
import { SignupComponent } from './signup/signup.component';

const routes: Routes = [
  { path: 'app-shortener', component: ShortenerComponent },
  { path: '', component: ShortenerComponent },
  { path: 'app-signup', component: SignupComponent },
  { path: 'app-login', component: LoginComponent },
  { path: 'app-myurls', component: MyurlsComponent },
  { path: 'l/:alias', component: RedirectComponent },
  { path: '**', component: NotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
