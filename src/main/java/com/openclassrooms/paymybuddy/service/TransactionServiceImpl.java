package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.utils.TransactionMapper;
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

    emitter.debit(request.getAmount());
    receiver.credit(request.getAmount());

    Transaction transaction = new Transaction(
        emitter,
        receiver,
        LocalDateTime.now(),
        request.getAmount(),
        request.getDescription()
    );

    Transaction savedTransaction = transactionRepository.save(transaction);
    return TransactionMapper.toDto(savedTransaction);
  }
}
