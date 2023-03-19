import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class LinkService {
  endpoint = environment.domain + "api/links";
  headers;

  constructor(public http: HttpClient, private authService: AuthService) { }

  getLink(alias: string): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.get(this.endpoint + "/" + alias, { headers: this.headers, observe: 'response' });
  }

  getLinks(): Observable<HttpResponse<Object>> {
    this.updateToken();

    if (this.authService.getToken() != null) {
      return this.http.get(this.endpoint, { headers: this.headers, observe: "response" });
    }

    return null;
  }

  addLink(formLink: NgForm): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.post(this.endpoint, formLink.value, { headers: this.headers, observe: "response" });
  }

  addLinkToGlobal(formLink: NgForm) {
    return this.http.post(this.endpoint, formLink.value, { headers: { 'Content-Type': 'application/json' }, observe: "response" });
  }

  addLinkView(alias): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.put(this.endpoint + "/" + alias + "/view", {}, { headers: this.headers, observe: "response" });
  }

  updateLink(link, previousAlias) {
    this.updateToken();
    return this.http.put(this.endpoint + "/" + previousAlias, link, { headers: this.headers, observe: "response" })
  }

  deleteLink(link: any): Observable<HttpResponse<Object>> {
    this.updateToken();
    return this.http.delete(this.endpoint + "/" + link.alias, { headers: this.headers, observe: "response" });
  }

  deleteAllLinks() {
    this.updateToken();
    return this.http.delete(this.endpoint, { headers: this.headers, observe: "response" });
  }

  copyToClipboard(link: any): void {
    document.addEventListener('copy', (e: ClipboardEvent) => {
      e.clipboardData.setData('text/plain', (environment.host + "l/" + link.alias));
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
    } else {
      this.headers = new HttpHeaders({
        'Content-Type': 'application/json'
      });
    }
  }
}
