package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing bank transfer between bank account and user wallet.
 */
@Service
public interface BankTransferService {

  Page<BankTransferDto> getAll(Pageable pageable);

  Page<BankTransferDto> getFromUser(int userId, Pageable pageable) throws ResourceNotFoundException;

  BankTransferDto requestTransfer(BankTransferDto request)
      throws ResourceNotFoundException, InsufficientProvisionException;

  void clearTransfersForAccount(BankAccount account);
}
