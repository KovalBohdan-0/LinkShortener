import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import User from '../user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  user!: User;
  message!: string;

  constructor(private http: HttpClient, private router: Router) { }

  logIn(formUser: NgForm): void {
    this.user = formUser.value;
    this.http.post('http://localhost:8080/api/login', this.user, { observe: 'response' }).subscribe((res) => {
      this.router.navigate(['/app-shortener'])
    }, (error) => {
      if (error.status == 404) {
        this.message = "Email or password incorrect !";
      } else {
        this.message = "Something went wrong !";
      }
    });
  }

  redirectToSignup():void {
    this.router.navigate(['/app-signup'])
  }
}
