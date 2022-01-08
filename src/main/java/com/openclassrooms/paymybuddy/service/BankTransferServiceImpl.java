package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.repository.BankTransferRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.BankTransferMapper;
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
public class BankTransferServiceImpl implements BankTransferService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankTransferServiceImpl.class);

  @Autowired
  BankTransferRepository bankTransferRepository;

  @Autowired
  UserRepository userRepository;

  @Override
  public Page<BankTransferDto> getAll(Pageable pageable) {
    return bankTransferRepository.findAll(pageable)
        .map(BankTransferMapper::toDto);
  }

  @Override
  public Page<BankTransferDto> getFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException {
    if (!userRepository.existsById(userId)) {
      LOGGER.error("This user is not found");
      throw new ResourceNotFoundException("This user is not found");
    }
    return bankTransferRepository.findByBankAccountUserUserId(userId, pageable)
        .map(BankTransferMapper::toDto);
  }

  @Override
  public BankTransferDto requestTransfer(BankTransferDto request)
      throws ResourceNotFoundException, InsufficientProvisionException {
    return null;
  }
}
