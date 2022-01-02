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

  List<BankAccountDto> getAllByUserId(int id) throws ResourceNotFoundException;

  List<BankAccountDto> addToUserId(int id, BankAccountDto account) throws ResourceNotFoundException;
}
