package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.utils.TransactionMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing bank transfer between bank account and user wallet.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired
  TransactionRepository transactionRepository;

  @Autowired
  UserService userService;

  @Override
  public Page<TransactionDto> getAll(Pageable pageable) {
    return transactionRepository.findAll(pageable)
        .map(TransactionMapper::toDto);
  }

  @Override
  public Page<TransactionDto> getFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException {
    User user = userService.getUserById(userId);
    return transactionRepository.findByEmitterOrReceiver(user, user, pageable)
        .map(TransactionMapper::toDto);
  }

  @Override
  @Transactional
  public TransactionDto requestTransaction(TransactionDto request)
      throws ResourceNotFoundException, InsufficientProvisionException {
    User emitter = userService.getUserById(request.getEmitterId());
    User receiver = userService.getUserById(request.getReceiverId());

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

  @Override
  public BigDecimal calculateFare(BigDecimal amount) {
    if (amount.signum() == -1) {
      throw new IllegalArgumentException("The amount can't be negative");
    }
    return amount
        .multiply(ApplicationValue.FARE_PERCENTAGE)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }

}
