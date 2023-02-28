import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import User from '../user';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  user: User = {
    email: "",
    password: ""
  };
  confirmPassword!: string;
  message!: string;

  constructor(private http: HttpClient, private router: Router) { }

  signUp(userForm: NgForm): void {
    this.user.email = userForm.value.email;
    this.user.password = userForm.value.password;
    console.log(userForm.errors)

    this.http.post('http://localhost:8080/api/users', this.user, { observe: 'response' }).subscribe((res) => {
      this.router.navigate(['/app-shortener'])
    }, (error) => {
      if (error.status == 409) {
        this.message = "Email is already exist !";
      } else {
        this.message = "Something went wrong !";
      }
    });
  }
}
