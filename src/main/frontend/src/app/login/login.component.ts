import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import User from '../user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  user!: User;
  message!: string;

  constructor(private router: Router, private authService: AuthService) { }

  logIn(formUser: NgForm): void {
    this.user = formUser.value;
    this.authService.logIn(this.user).subscribe({
      next: (response: any) => {
        console.log(response.body.jwt);
        this.authService.storeToken(response.body.jwt);
        this.router.navigate(['/app-shortener']);
      },
      error: (error) => {
        if (error.status == 404) {
          this.message = "Email or password incorrect !";
        } else {
          this.message = "Something went wrong !";
        }
      }
    });
  }

  redirectToSignup(): void {
    this.router.navigate(['/app-signup'])
  }
}
