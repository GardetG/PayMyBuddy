package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service interface for managing user's bank account.
 */
@Service
public interface BankAccountService {

  List<BankAccountDto> getAllByUserId(int userId) throws ResourceNotFoundException;

  List<BankAccountDto> addToUserId(int userId, BankAccountDto account)
      throws ResourceNotFoundException;

  List<BankAccountDto> deleteById(int userId, int id) throws ResourceNotFoundException;
}
