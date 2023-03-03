import { Component } from '@angular/core';

@Component({
  selector: 'app-myurls',
  templateUrl: './myurls.component.html',
  styleUrls: ['./myurls.component.css']
})
export class MyurlsComponent {
  currentLinks = []; 
  links = [
    { fullLink: "https://www.youtube.com/", alias: "localhost:4200/l/youtube" },
    { fullLink: "https://www.twitch.tv/", alias: "localhost:4200/l/twitch" },
    { fullLink: "https://www.youtube.com/2", alias: "localhost:4200/l/youtube2" },
    { fullLink: "https://www.twitch.tv/2", alias: "localhost:4200/l/twitch2" }
  ];
  page = 1;
  pageSize = 4;
  collectionSize = this.links.length;

  constructor() {
    this.updateLinks();
  }

  updateLinks() {
    this.currentLinks = this.links.map((link, i) => ({ id: i + 1, ...link })).slice(
			(this.page - 1) * this.pageSize,
			(this.page - 1) * this.pageSize + this.pageSize,
		);
  }
}
