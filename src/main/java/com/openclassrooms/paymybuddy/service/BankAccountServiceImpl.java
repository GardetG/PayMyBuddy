package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.BankAccountMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
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
  private static  final BigDecimal DEFAULT_ACCOUNT_BALANCE = BigDecimal.valueOf(100);

  @Autowired
  private UserRepository userRepository;

  @Override
  public List<BankAccountDto> getAllByUserId(int userId) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    return user.get().getBankAccounts().stream()
        .map(BankAccountMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public List<BankAccountDto> addToUserId(int userId, BankAccountDto account)
      throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    BankAccount bankAccountToAdd = BankAccountMapper.toModel(account);
    bankAccountToAdd.setBalance(DEFAULT_ACCOUNT_BALANCE);

    user.get().getBankAccounts().add(bankAccountToAdd);
    User savedUser = userRepository.save(user.get());

    return savedUser.getBankAccounts().stream()
        .map(BankAccountMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<BankAccountDto> deleteById(int userId, int id) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    Optional<BankAccount> accountToDelete = user.get().getBankAccounts().stream()
        .filter(account -> account.getBankAccountId() == id)
        .findFirst();
    if (accountToDelete.isEmpty()) {
      LOGGER.error("This account is not found: {}", id);
      throw new ResourceNotFoundException("This account is not found");
    }

    user.get().getBankAccounts().remove(accountToDelete.get());
    User savedUser = userRepository.save(user.get());

    return savedUser.getBankAccounts().stream()
        .map(BankAccountMapper::toDto)
        .collect(Collectors.toList());
  }

}
