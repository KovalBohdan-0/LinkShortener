import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { LinkService } from '../link.service';

@Component({
  selector: 'app-redirect',
  templateUrl: './redirect.component.html',
  styleUrls: ['./redirect.component.css']
})
export class RedirectComponent {
  constructor(private activeRoute: ActivatedRoute, private authService: AuthService, private http: HttpClient, private router: Router, private linkService: LinkService) {
    let params: any = activeRoute.params;
    let alias = params.value.alias;

    this.getLink(alias);
  }

  getLink(alias: string) {
    this.linkService.addLinkView(alias).subscribe();
    this.linkService.getLink(alias).subscribe({
      next: (response: any) => {
        window.location.href = response.body.fullLink;
      },
      error: (error) => {
        this.router.navigate(['/app-not-found']);
      }
    });
  }
}
