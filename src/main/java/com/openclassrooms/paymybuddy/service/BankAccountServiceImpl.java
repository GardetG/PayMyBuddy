package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ErrorMessage;
import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.BankAccountMapper;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing user's bank accounts.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountServiceImpl.class);

  @Autowired
  private UserRepository userRepository;

  @Override
  public List<BankAccountDto> getAllFromUser(int userId) throws ResourceNotFoundException {
    User user = getUserById(userId);
    return user.getBankAccounts().stream()
        .map(BankAccountMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public BankAccountDto addToUser(int userId, BankAccountDto account)
      throws ResourceNotFoundException, ResourceAlreadyExistsException {

    User user = getUserById(userId);
    BankAccount bankAccountToAdd = BankAccountMapper.toModel(account);

    user.addBankAccount(bankAccountToAdd);
    user = userRepository.save(user);

    BankAccount bankAccountCreated = findBankAccount(user.getBankAccounts(),
        a -> a.getIban().equals(account.getIban()));
    return BankAccountMapper.toDto(bankAccountCreated);
  }

  @Override
  public void removeFromUser(int userId, int id) throws ResourceNotFoundException {

    User user = getUserById(userId);
    BankAccount bankAccountToDelete = findBankAccount(user.getBankAccounts(),
        a -> a.getBankAccountId() == id);

    user.removeBankAccount(bankAccountToDelete);
    userRepository.save(user);
  }

  private User getUserById(int userId) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(ErrorMessage.USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    return user.get();
  }

  private BankAccount findBankAccount(Collection<BankAccount> bankAccounts,
                                     Predicate<BankAccount> predicate)
      throws ResourceNotFoundException {
    return bankAccounts.stream()
        .filter(predicate)
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error(ErrorMessage.BANKACCOUNT_NOT_FOUND);
          return new ResourceNotFoundException(ErrorMessage.BANKACCOUNT_NOT_FOUND);
        });
  }

}
