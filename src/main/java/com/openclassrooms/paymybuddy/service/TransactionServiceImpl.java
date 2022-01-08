package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.TransactionMapper;
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
public class TransactionServiceImpl implements TransactionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

  @Autowired
  TransactionRepository transactionRepository;

  @Autowired
  UserRepository userRepository;

  @Override
  public Page<TransactionDto> getAll(Pageable pageable) {
    return transactionRepository.findAll(pageable)
        .map(TransactionMapper::toDto);
  }

  @Override
  public Page<TransactionDto> getFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("This user is not found"));

    return transactionRepository.findByEmitterOrReceive(user, user, pageable)
        .map(TransactionMapper::toDto);
  }

  @Override
  public TransactionDto requestTansaction(TransactionDto request)
      throws ResourceNotFoundException, InsufficientProvisionException {
    return null;
  }
}
