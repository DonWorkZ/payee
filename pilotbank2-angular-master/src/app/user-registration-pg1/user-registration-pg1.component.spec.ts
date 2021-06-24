import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRegistrationPg1Component } from './user-registration-pg1.component';

describe('UserRegistrationPg1Component', () => {
  let component: UserRegistrationPg1Component;
  let fixture: ComponentFixture<UserRegistrationPg1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserRegistrationPg1Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRegistrationPg1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
