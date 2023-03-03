import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyurlsComponent } from './myurls.component';

describe('MyurlsComponent', () => {
  let component: MyurlsComponent;
  let fixture: ComponentFixture<MyurlsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyurlsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyurlsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
