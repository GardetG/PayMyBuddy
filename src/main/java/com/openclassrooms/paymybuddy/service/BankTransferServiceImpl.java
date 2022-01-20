package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.BankTransfer;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.BankTransferRepository;
import com.openclassrooms.paymybuddy.utils.BankTransferMapper;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing bank transfer between bank account and user wallet.
 */
@Service
public class BankTransferServiceImpl implements BankTransferService, UserDeletionObserver {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankTransferServiceImpl.class);

  @Autowired
  BankTransferRepository bankTransferRepository;

  @Autowired
  UserService userService;

  /**
   * Subscribe to the userService to get notify on user deletion.
   */
  @PostConstruct
  protected void userDeletionSubscribe() {
    userService.userDeletionSubscribe(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<BankTransferDto> getAll(Pageable pageable) {
    return bankTransferRepository.findAll(pageable)
        .map(BankTransferMapper::toDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<BankTransferDto> getFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException {
    User user = userService.retrieveEntity(userId);
    return bankTransferRepository.findByBankAccountIn(user.getBankAccounts(), pageable)
        .map(BankTransferMapper::toDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public BankTransferDto requestTransfer(BankTransferDto request)
      throws ResourceNotFoundException, InsufficientProvisionException {

    User user = userService.retrieveEntity(request.getUserId());
    BankAccount account = findAccountById(user, request.getBankAccountId());

    if (request.isIncome()) {
      account.debit(request.getAmount());
      user.credit(request.getAmount());
    } else {
      user.debit(request.getAmount());
      account.credit(request.getAmount());
    }

    BankTransfer bankTransfer = new BankTransfer(
        account,
        LocalDateTime.now(),
        request.getAmount(),
        request.isIncome()
    );

    BankTransfer savedBankTransfer = bankTransferRepository.save(bankTransfer);
    return BankTransferMapper.toDto(savedBankTransfer);
  }

  /**
   * Method called by observer pattern on user deletion.
   * Call the clearing of transfers for each account of the user.
   */
  @Override
  public void onUserDeletion(User user) {
    user.getBankAccounts().forEach(this::clearTransfersForAccount);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void clearTransfersForAccount(BankAccount account) {
    List<BankTransfer> transfersList = bankTransferRepository.findByBankAccount(account);
    transfersList.forEach(transfer -> bankTransferRepository.delete(transfer));
  }

  private BankAccount findAccountById(User user, int accountId)
      throws ResourceNotFoundException {
    return user.getBankAccounts().stream()
        .filter(a -> a.getBankAccountId() == accountId)
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("This bank account is not found");
          return new ResourceNotFoundException("This bank account is not found");
        });
  }
}
