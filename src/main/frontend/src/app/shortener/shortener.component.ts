import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Link } from '../link';
import User from '../user';


@Component({
  selector: 'app-shortener',
  templateUrl: './shortener.component.html',
  styleUrls: ['./shortener.component.css']
})

export class ShortenerComponent {
  link: Link = {
    fullLink: 'link',
    alias: 'alias'
  }

  user: User = {
    email: "email",
    password: "pass"
  }

  constructor(private http: HttpClient) { }

  shortLink(formLink: NgForm): void {
    const headers = new HttpHeaders().set('Content-Type', 'text/plain');

    console.log(formLink.value);
    this.link.fullLink = formLink.value.url;
    this.link.alias = formLink.value.alias;

    // const req = this.http.post('http://localhost:8080/api/users', JSON.stringify(this.user), {headers}).subscribe(response => console.log(response));
    const req2 = this.http.get('http://localhost:8080/api/links/youtube', {headers: headers, observe: "response"}).subscribe(
      response => { 
        console.log(response.body);
      });
    // console.log(req2);
  }
}

