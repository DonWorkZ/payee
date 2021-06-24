import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRegistrationPg3Component } from './user-registration-pg3.component';

describe('UserRegistrationPg3Component', () => {
  let component: UserRegistrationPg3Component;
  let fixture: ComponentFixture<UserRegistrationPg3Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserRegistrationPg3Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRegistrationPg3Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
