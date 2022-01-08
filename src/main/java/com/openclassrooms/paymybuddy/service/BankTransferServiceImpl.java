package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.BankTransfer;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.BankTransferRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.BankTransferMapper;
import java.time.LocalDateTime;
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
  @Transactional
  public BankTransferDto requestTransfer(BankTransferDto request)
      throws ResourceNotFoundException, InsufficientProvisionException {

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> {
          LOGGER.error("This user is not found");
          return new ResourceNotFoundException("This user is not found");
        });
    BankAccount account = user.getBankAccounts().stream()
        .filter(a -> a.getBankAccountId() == request.getBankAccountId())
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("This account is not found");
          return new ResourceNotFoundException("This account is not found");
        });

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

    BankTransfer savedBankTransfer =  bankTransferRepository.save(bankTransfer);
    return BankTransferMapper.toDto(savedBankTransfer);
  }
}
