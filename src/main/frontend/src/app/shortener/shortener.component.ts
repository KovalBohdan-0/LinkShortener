import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Link } from '../link';


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

  constructor(private http: HttpClient) { }

  shortLink(formLink: NgForm): void {
    this.link = formLink.value;
    this.http.post('http://localhost:8080/api/links', this.link, { observe: 'response' }).subscribe((res) => {
      this.successMessage = "Link was saved";
    }, (error) => {
      if (error.status = 409) {
        this.message = "Link with this alias already exist";
      } else {
        this.message = "Something went wrong !";
      }
    });
  }
}

