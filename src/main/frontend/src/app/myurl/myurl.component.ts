import { Component, Input } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LinkService } from '../link.service';

@Component({
  selector: 'app-myurl',
  templateUrl: './myurl.component.html',
  styleUrls: ['./myurl.component.css']
})
export class MyurlComponent {
  @Input() link;
  @Input("currentLinks") links: any;
  updateResult: string;


  constructor(private linkService: LinkService, private modalService: NgbModal) {

  }

  copyToClipboard(link: any): void {
    document.addEventListener('copy', (e: ClipboardEvent) => {
      e.clipboardData.setData('text/plain', ("http://localhost:4200/l/" + link.alias));
      e.preventDefault();
      document.removeEventListener('copy', null);
    });
    document.execCommand('copy');
  }

  deleteLink(link) {
    this.linkService.deleteLink(link).subscribe(response => console.log(response));
    this.links.splice(this.links.indexOf(link), 1);
  }

  getFavicon(link: any): string {
    // return "https://s2.googleusercontent.com/s2/favicons?domain_url=" + link.fullLink;
    return "";
  }

  openModal(content) {
		this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then(
			(result) => {
				this.updateResult = result;
			},
			(reason) => {
			},
		);
	}

  updateLink(link) {
    let newLink = link.value;

    if(newLink.alias == "") {
      newLink.alias = this.link.alias;
    } else if (newLink.fullLink == "") {
      newLink.fullLink = this.link.fullLink;
    }

    this.linkService.updateLink(newLink, this.link.alias).subscribe(response => console.log(response));
    //TODO
  }
}
