package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user's bank account.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public List<BankAccountDto> getAllByUserId(int id) {
    return null;
  }
}
