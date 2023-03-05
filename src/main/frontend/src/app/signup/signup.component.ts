import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
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

  constructor(private router: Router, private authService: AuthService) { }

  signUp(userForm: NgForm): void {
    this.user.email = userForm.value.email;
    this.user.password = userForm.value.password;

    this.authService.signUp(this.user).subscribe({
      next: (response: any) => {
        this.authService.storeToken(response.body.jwt);
        this.router.navigate(['/app-shortener'])
      },
      error: (error) => {
        if (error.status == 409) {
          this.message = "Email is already exist !";
        } else {
          this.message = "Something went wrong !";
        }
      }
    });
  }
}
