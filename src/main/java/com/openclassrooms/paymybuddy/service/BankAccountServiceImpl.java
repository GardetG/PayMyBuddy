package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.utils.BankAccountMapper;
import com.openclassrooms.paymybuddy.utils.PaginateCollection;
import java.util.Collection;
import java.util.function.Predicate;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing user's bank accounts.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountServiceImpl.class);

  @Autowired
  private UserService userService;

  @Autowired
  private BankTransferService bankTransferService;

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<BankAccountDto> getAllFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException {

    User user = userService.retrieveEntity(userId);
    Page<BankAccount> page = PaginateCollection.paginate(user.getBankAccounts(), pageable);
    return page.map(BankAccountMapper::toDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public BankAccountDto addToUser(int userId, BankAccountDto account)
      throws ResourceNotFoundException, ResourceAlreadyExistsException {

    User user = userService.retrieveEntity(userId);
    BankAccount bankAccountToAdd = BankAccountMapper.toModel(account);

    user.addBankAccount(bankAccountToAdd);
    userService.saveEntity(user);

    BankAccount bankAccountCreated = findBankAccount(user.getBankAccounts(),
        a -> a.getIban().equals(account.getIban()));
    return BankAccountMapper.toDto(bankAccountCreated);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void removeFromUser(int userId, int id) throws ResourceNotFoundException {
    User user = userService.retrieveEntity(userId);
    BankAccount bankAccountToDelete = findBankAccount(user.getBankAccounts(),
        a -> a.getBankAccountId() == id);

    bankTransferService.clearTransfersForAccount(bankAccountToDelete);
    user.removeBankAccount(bankAccountToDelete);

    userService.saveEntity(user);
  }

  private BankAccount findBankAccount(Collection<BankAccount> bankAccounts,
                                      Predicate<BankAccount> predicate)
      throws ResourceNotFoundException {
    return bankAccounts.stream()
        .filter(predicate)
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("This bank account is not found");
          return new ResourceNotFoundException("This bank account is not found");
        });
  }
}
