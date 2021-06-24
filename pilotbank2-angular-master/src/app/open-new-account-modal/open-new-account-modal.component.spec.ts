import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OpenNewAccountModalComponent } from './open-new-account-modal.component';

describe('OpenNewAccountModalComponent', () => {
  let component: OpenNewAccountModalComponent;
  let fixture: ComponentFixture<OpenNewAccountModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OpenNewAccountModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenNewAccountModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
