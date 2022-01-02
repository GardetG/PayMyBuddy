package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.BankAccountMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user's bank account.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountServiceImpl.class);

  private static final String USER_NOT_FOUND = "This user is not found";

  @Autowired
  private UserRepository userRepository;

  @Override
  public List<BankAccountDto> getAllByUserId(int id) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      LOGGER.error(USER_NOT_FOUND + ": {}", id);
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    return user.get().getBankAccounts().stream()
        .map(BankAccountMapper::toDto)
        .collect(Collectors.toList());
  }

}
