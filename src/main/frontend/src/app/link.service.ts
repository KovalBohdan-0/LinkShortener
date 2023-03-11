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

  constructor(public http: HttpClient, private authService: AuthService) { }

  getLink(alias: string): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.get(this.domain + "/" + alias, { headers: this.headers, observe: 'response' });
  }

  getLinks(): Observable<HttpResponse<Object>> {
    this.updateToken();
    
    if (this.authService.getToken() != null) {
      return this.http.get(this.domain, { headers: this.headers, observe: "response" });
    }

    return null;
  }

  addLink(formLink: NgForm): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.post(this.domain, formLink.value, { headers: this.headers, observe: "response" });
  }

  addLinkToGlobal(formLink: NgForm) {
    return this.http.post(this.domain, formLink.value, { headers: {'Content-Type': 'application/json'}, observe: "response" });
  }

  addLinkView(alias): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.put(this.domain + "/" + alias + "/view",{} , { headers: this.headers, observe: "response" });
  }

  updateLink(link, previousAlias) {
    this.updateToken();
    return this.http.put(this.domain + "/" + previousAlias, link, { headers: this.headers, observe: "response" })
  }

  deleteLink(link: any): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.delete(this.domain + "/" + link.alias, { headers: this.headers, observe: "response" });
  }

  deleteAllLinks() {
    this.updateToken();
    return this.http.delete(this.domain, { headers: this.headers, observe: "response" });
  }

  copyToClipboard(link: any): void {
    document.addEventListener('copy', (e: ClipboardEvent) => {
      e.clipboardData.setData('text/plain', ("http://localhost:4200/l/" + link.alias));
      e.preventDefault();
      document.removeEventListener('copy', null);
    });
    document.execCommand('copy');
  }

  updateToken() {
    if (this.authService.getToken() != null) {
      this.headers = new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + this.authService.getToken()
      });
    }  
  }
}
