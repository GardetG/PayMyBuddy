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

  List<BankAccountDto> getAllFromUser(int userId) throws ResourceNotFoundException;

  BankAccountDto addToUser(int userId, BankAccountDto account)
      throws ResourceNotFoundException, ResourceAlreadyExistsException;

  void removeFromUser(int userId, int id) throws ResourceNotFoundException;
}
