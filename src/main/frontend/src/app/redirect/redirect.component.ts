import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-redirect',
  templateUrl: './redirect.component.html',
  styleUrls: ['./redirect.component.css']
})
export class RedirectComponent {
  constructor(private activeRoute: ActivatedRoute, private authService: AuthService, private http: HttpClient, private router: Router) {
    let params: any = activeRoute.params;
    let alias = params.value.alias;

    this.getLink(alias);
  }

  getLink(alias: string) {
    let headers;
    
    if (this.authService.getToken() != null) {
      headers = new HttpHeaders({
        'Authorization': "Bearer " + this.authService.getToken()
      });
    }
   
    this.http.get('http://localhost:8080/api/links/' + alias, { headers: headers, observe: 'response'}).subscribe({
      next: (response: any) => {
        window.location.href = response.body.fullLink;
      },
      error: (error) => {
        this.router.navigate(['/app-not-found']);
      }
    });
  }
}
