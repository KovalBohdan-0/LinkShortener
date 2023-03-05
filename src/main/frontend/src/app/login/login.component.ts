import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { ToastService } from '../toast.service';
import User from '../user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  user!: User;
  message!: string;

  constructor(private router: Router, private authService: AuthService, public toastService: ToastService) { }

  logIn(formUser: NgForm): void {
    this.user = formUser.value;
    this.authService.logIn(this.user).subscribe({
      next: (response: any) => {
        this.authService.storeToken(response.body.jwt);
        this.toastService.addSuccess("Login", "Successfully logged")
        this.router.navigate(['/app-shortener']);
      },
      error: (error) => {
        if (error.status == 404) {
          this.toastService.addError("Login error", "Email or password is incorrect !")
        } else {
          this.toastService.addError("Login error", "Something went wrong Something went wrong !")
        }
      }
    });
  }

  redirectToSignup(): void {
    this.router.navigate(['/app-signup'])
  }
}
