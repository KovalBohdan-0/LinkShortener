import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class LinkService {
  domain = "http://localhost:8080/api/links";
  headers;

  constructor(public http: HttpClient, private authService: AuthService) {
    if (authService.getToken() != null) {
      this.headers = new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + this.authService.getToken()
      });
    }
  }

  getLink(alias: string): Observable<HttpResponse<Object>> {
    return this.http.get(this.domain + "/" + alias, { headers: this.headers, observe: 'response' });
  }

  getLinks(): Observable<HttpResponse<Object>> {
    if (this.authService.getToken() != null) {
      return this.http.get(this.domain, { headers: this.headers, observe: "response" });
    }

    console.log("todo add links to local storage");
    return null;
  }

  addLink(formLink: NgForm): Observable<HttpResponse<Object>> {
    return this.http.post(this.domain, formLink.value, { headers: this.headers, observe: "response" });
  }

  updateLink(link, previousAlias) {
    return this.http.put(this.domain + "/" + previousAlias, link, { headers: this.headers, observe: "response" })
  }

  deleteLink(link: any): Observable<HttpResponse<Object>> {
    return this.http.delete(this.domain + "/" + link.alias, { headers: this.headers, observe: "response" });
  }
}
