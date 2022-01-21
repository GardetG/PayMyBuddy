package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.utils.TransactionMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing bank transfer between bank account and user wallet.
 */
@Service
public class TransactionServiceImpl implements TransactionService, UserDeletionObserver {

  @Autowired
  TransactionRepository transactionRepository;

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
  public Page<TransactionDto> getAll(Pageable pageable) {
    return transactionRepository.findAll(pageable)
        .map(TransactionMapper::toDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<TransactionDto> getFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException {
    User user = userService.retrieveEntity(userId);
    return transactionRepository.findByEmitterOrReceiver(user, user, pageable)
        .map(TransactionMapper::toDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackOn = ForbiddenOperationException.class)
  public TransactionDto requestTransaction(TransactionDto request)
      throws ResourceNotFoundException, ForbiddenOperationException {
    User emitter = userService.retrieveEntity(request.getEmitterId());
    User receiver = userService.retrieveEntity(request.getReceiverId());

    BigDecimal amount = request.getAmount();
    emitter.debit(amount.add(calculateFare(amount)));
    receiver.credit(amount);

    Transaction transaction = new Transaction(
        emitter,
        receiver,
        LocalDateTime.now(),
        amount,
        request.getDescription()
    );

    Transaction savedTransaction = transactionRepository.save(transaction);
    return TransactionMapper.toDto(savedTransaction);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal calculateFare(BigDecimal amount) {
    if (amount.signum() == -1) {
      throw new IllegalArgumentException("The amount can't be negative");
    }
    return amount
        .multiply(ApplicationValue.FARE_PERCENTAGE)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void clearTransactionForUser(Transaction transaction, User user) {
    if (user.equals(transaction.getEmitter())) {
      transaction.setEmitter(null);
    }
    if (user.equals(transaction.getReceiver())) {
      transaction.setReceiver(null);
    }
    if (transaction.getEmitter() == null && transaction.getReceiver() == null) {
      transactionRepository.delete(transaction);
      return;
    }
    transactionRepository.save(transaction);
  }

  /**
   * Method called by observer pattern on user deletion.
   * Call the clearing of transaction for each of the user's transactions.
   */
  @Override
  @Transactional
  public void onUserDeletion(User user) {
    transactionRepository.findByEmitterOrReceiver(user, user)
        .forEach(transaction -> clearTransactionForUser(transaction, user));
  }
}
