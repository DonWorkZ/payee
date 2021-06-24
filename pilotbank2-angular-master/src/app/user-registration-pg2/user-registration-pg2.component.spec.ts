import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRegistrationPg2Component } from './user-registration-pg2.component';

describe('UserRegistrationPg2Component', () => {
  let component: UserRegistrationPg2Component;
  let fixture: ComponentFixture<UserRegistrationPg2Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserRegistrationPg2Component ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRegistrationPg2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
