import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyurlComponent } from './myurl.component';

describe('MyurlComponent', () => {
  let component: MyurlComponent;
  let fixture: ComponentFixture<MyurlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyurlComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyurlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
