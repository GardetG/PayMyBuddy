package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service interface for managing user's bank account.
 */
@Service
public interface BankAccountService {

  List<BankAccountDto> getAllByUserId(int id);

}
