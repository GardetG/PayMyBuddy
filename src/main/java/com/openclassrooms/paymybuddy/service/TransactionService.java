package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing transactions between users.
 */
@Service
public interface TransactionService {

  Page<TransactionDto> getAll(Pageable pageable);

  Page<TransactionDto> getFromUser(int userId, Pageable pageable) throws ResourceNotFoundException;

  TransactionDto requestTransaction(TransactionDto request)
      throws ResourceNotFoundException, InsufficientProvisionException;

  BigDecimal calculateFare(BigDecimal amount);

  void clearTransactionForUser(Transaction transaction, User user);
}