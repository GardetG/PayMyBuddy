package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
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

  /**
   * Get a page of the list of all bank transfer in the form of DTO.
   *
   * @param pageable for the requested page
   * @return Page of BankTransferDto
   */
  Page<BankTransferDto> getAll(Pageable pageable);

  /**
   * Get a oage of the list of all bank transfer of a user in the form DTO.
   *
   * @param userId of the user
   * @param pageable of the requested page
   * @return Page of BankTransferdto
   * @throws ResourceNotFoundException if user not found
   */
  Page<BankTransferDto> getFromUser(int userId, Pageable pageable) throws ResourceNotFoundException;

  /**
   * Request a bank transfer according to the data send in the form of DTO with userId, accountId,
   * amount of the transfer and if the transfer is crediting or debiting the user wallet.
   * The user and account will be credited or debited and the transfer saved in the database.
   *
   * @param request of the transfer
   * @return BankTransferDtO of the performed transfer
   * @throws ResourceNotFoundException if user or bank account not found
   * @throws InsufficientProvisionException if provision insufficient to perform the transfer
   */
  BankTransferDto requestTransfer(BankTransferDto request)
      throws ResourceNotFoundException, ForbiddenOperationException;

  /**
   * Delete from the database all bank transfer records for the bank account specified.
   *
   * @param account whose transfers must be clear
   */
  void clearTransfersForAccount(BankAccount account);
}
