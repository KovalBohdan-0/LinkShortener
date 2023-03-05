import { Injectable, TemplateRef } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toasts = [];

  constructor() { }

  add(header: string, body: string | TemplateRef<any>, options: any = {}) {
    this.toasts.push({ header, body, ...options });
  }

  addSuccess(header: string, body: string ) {
		this.add(header, body, { classname: 'bg-success text-light toast', delay: 10000 });
	}

  addError(header: string, body: string) {
		this.add(header, body, { classname: 'bg-danger text-light toast', delay: 15000 });
	}

  remove(toast) {
    this.toasts.splice(this.toasts.indexOf(toast), 1);
  }
}
