import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IdentificationDocumentsComponent } from './identification-documents.component';

describe('IdentificationDocumentsComponent', () => {
  let component: IdentificationDocumentsComponent;
  let fixture: ComponentFixture<IdentificationDocumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IdentificationDocumentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IdentificationDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
