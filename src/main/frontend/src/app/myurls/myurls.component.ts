import { Component } from '@angular/core';
import { LinkService } from '../link.service';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-myurls',
  templateUrl: './myurls.component.html',
  styleUrls: ['./myurls.component.css']
})
export class MyurlsComponent {
  toasts = [];
  currentLinks = [];
  links = [];
  page = 1;
  pageSize = 4;
  collectionSize = this.links.length;

  constructor(private linkService: LinkService, public toastService: ToastService) {
    this.getLinks();
  }

  getLinks() {
    this.linkService.getLinks().subscribe({
      next: (response: any) => {
        this.links = response.body;
        this.updateLinks();
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  updateLinks() {
    this.currentLinks = this.links.map((link, i) => ({ id: i + 1, ...link })).slice(
      (this.page - 1) * this.pageSize,
      (this.page - 1) * this.pageSize + this.pageSize,
    );
    this.collectionSize = this.links.length;
  }
}
