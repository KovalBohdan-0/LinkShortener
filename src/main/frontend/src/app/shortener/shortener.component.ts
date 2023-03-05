import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
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
  message!: string;
  successMessage!: string;

  constructor(private linkService: LinkService, public toastService: ToastService) { }

  shortLink(formLink: NgForm): void {
    if (formLink.value.alias == "") {
      formLink.value.alias = this.setRandomAlias();
    }

    this.link = formLink.value;
    this.linkService.addLink(formLink).subscribe({
      next: (response) => {
        this.toastService.addSuccess("Creating short link", "Link was saved. Short link: localhost:4200/l/" + this.link.alias);
      },
      error: (error) => {
        if (error.status == 409) {
          this.toastService.addError("Creating short link", "Link with this alias already exist");
        } else {
          this.toastService.addError("Creating short link", "Something went wrong !");
        }
      }
    });
  }

  randomString(length): string {
    let randomChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';

    for (let i = 0; i < length; i++) {
      result += randomChars.charAt(Math.floor(Math.random() * randomChars.length));
    }

    return result;
  }

  setRandomAlias() {
    let randomAlias = this.randomString(5);
    this.linkService.getLink(randomAlias).subscribe(response => {
      if (response.body != null) {
        this.setRandomAlias();
      } 
    });

    return randomAlias;
  }
}

