import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRegistrationPg4Component } from './user-registration-pg4.component';

describe('UserRegistrationPg4Component', () => {
  let component: UserRegistrationPg4Component;
  let fixture: ComponentFixture<UserRegistrationPg4Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserRegistrationPg4Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRegistrationPg4Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
