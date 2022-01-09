import { TestBed } from '@angular/core/testing';

import { ApiPaymybuddyService } from './api-paymybuddy.service';

describe('ApiPaymybuddyService', () => {
  let service: ApiPaymybuddyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiPaymybuddyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
