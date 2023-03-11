import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Link } from '../link';
import { LinkService } from '../link.service';
import { ToastService } from '../toast.service';


@Component({
  selector: 'app-shortener',
  templateUrl: './shortener.component.html',
  styleUrls: ['./shortener.component.css']
})

export class ShortenerComponent {
  link: Link = {
    fullLink: "",
    alias: ""
  }
  isChecked = true;
  message!: string;
  successMessage!: string;

  constructor(private linkService: LinkService, public toastService: ToastService, private authService: AuthService) { }

  shortLink(formLink: NgForm): void {
    let aliasIsEmpty;

    if (formLink.value.alias == "") {
      aliasIsEmpty = true;
      formLink.value.alias = this.randomString(5);
    }

    this.link = formLink.value;
    this.linkService.addLink(formLink).subscribe({
      next: (response) => {
        this.toastService.addSuccess("Creating short link", "Link was saved and copied. Short link: localhost:4200/l/" + this.link.alias);
        this.linkService.copyToClipboard(this.link);
      },
      error: (error) => {
        if (error.status == 409) {
          if (aliasIsEmpty) {
            formLink.value.alias = "";
            this.shortLink(formLink);
          }

          this.toastService.addError("Creating short link", "Link with this alias already exist");
        } else {
          this.toastService.addError("Creating short link", "Something went wrong !");
        }
      }
    });

    this.addLinkToGlobal(formLink);
  }

  addLinkToGlobal(formLink: NgForm) {
    if (this.getToken() != null && this.isChecked == true) {
      this.linkService.addLinkToGlobal(formLink).subscribe({
        next: (response) => {
          this.toastService.addSuccess("Creating global link", "Global link was saved and copied. Short link: localhost:4200/l/" + this.link.alias);

          formLink.value.alias = "";
        },
        error: (error) => {
          if (error.status == 409) {
            this.toastService.addError("Creating global link", "Link with this alias already exist");
          } else {
            this.toastService.addError("Creating global link", "Something went wrong !");
          }
        }
      });
    }
  }

  randomString(length): string {
    return self.crypto.randomUUID().toString().substring(0, length);
  }

  getToken(): string {
    return this.authService.getToken();
  }
}

