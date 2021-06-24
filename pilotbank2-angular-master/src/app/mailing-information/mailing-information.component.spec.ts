import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MailingInformationComponent } from './mailing-information.component';

describe('MailingInformationComponent', () => {
  let component: MailingInformationComponent;
  let fixture: ComponentFixture<MailingInformationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MailingInformationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MailingInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
