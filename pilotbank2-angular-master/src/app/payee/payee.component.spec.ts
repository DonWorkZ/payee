import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PayeeComponent } from './payee.component';

fdescribe('PayeeComponent', () => {
  let component: PayeeComponent;
  let fixture: ComponentFixture<PayeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PayeeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PayeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
