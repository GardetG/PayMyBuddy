package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing user's bank accounts.
 */
@Service
public interface BankAccountService {

  List<BankAccountDto> getAllByUserId(int userId) throws ResourceNotFoundException;

  BankAccountDto addToUserId(int userId, BankAccountDto account)
      throws ResourceNotFoundException, ResourceAlreadyExistsException;

  void deleteById(int userId, int id) throws ResourceNotFoundException;
}
