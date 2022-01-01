package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service class for managing user's bank account.
 */
public class BankAccountServiceImpl implements BankAccountService {

  @Autowired
  private UserRepository userRepository;

}
