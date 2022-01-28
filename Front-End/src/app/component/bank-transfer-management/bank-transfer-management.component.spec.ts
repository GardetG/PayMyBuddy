import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankTransferManagementComponent } from './bank-transfer-management.component';

describe('BankTransferManagementComponent', () => {
  let component: BankTransferManagementComponent;
  let fixture: ComponentFixture<BankTransferManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BankTransferManagementComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BankTransferManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
