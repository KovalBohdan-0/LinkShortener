import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { ToastService } from '../toast.service';
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

  constructor(private router: Router, private authService: AuthService, private toastService: ToastService) { }

  signUp(userForm: NgForm): void {
    this.user.email = userForm.value.email;
    this.user.password = userForm.value.password;

    this.authService.signUp(this.user).subscribe({
      next: (response: any) => {
        console.log(response);
        this.authService.storeToken(response.body.jwt);
        this.toastService.addSuccess("Creating user", "Successfully created user");
        this.router.navigate(['/app-shortener']);
      },
      error: (error) => {
        if (error.status == 409) {
          this.toastService.addError("Creating user", "User with email: " + this.user.email + " already exist");
        } else {
          this.toastService.addError("Creating user", "Something went wrong !");        }
      }
    });
  }
}
