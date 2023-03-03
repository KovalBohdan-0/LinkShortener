import { Component, Input } from '@angular/core';
import { Link } from '../link';

@Component({
  selector: 'app-myurl',
  templateUrl: './myurl.component.html',
  styleUrls: ['./myurl.component.css']
})
export class MyurlComponent {
  @Input() link;

}
