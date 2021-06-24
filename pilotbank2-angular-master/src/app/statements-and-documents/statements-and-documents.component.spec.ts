import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatementsAndDocumentsComponent } from './statements-and-documents.component';

describe('StatementsAndDocumentsComponent', () => {
  let component: StatementsAndDocumentsComponent;
  let fixture: ComponentFixture<StatementsAndDocumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatementsAndDocumentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatementsAndDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
