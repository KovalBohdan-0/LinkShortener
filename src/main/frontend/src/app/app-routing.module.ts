import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ShortenerComponent } from './shortener/shortener.component';
import { SignupComponent } from './signup/signup.component';

const routes: Routes = [
  { path: 'app-shortener', component: ShortenerComponent },
  { path: '', component: ShortenerComponent },
  { path: 'app-signup', component: SignupComponent },
  { path: 'app-login', component: LoginComponent },
  { path: '**', component: ShortenerComponent }, //TODO add 404 page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
