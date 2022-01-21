package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
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

  /**
   * Get a page of the list of all transactions between users in the form of DTO.
   *
   * @param pageable for the requested page
   * @return Page of TransactionDto
   */
  Page<TransactionDto> getAll(Pageable pageable);

  /**
   * Get a oage of the list of all transactions involving a user in the form DTO.
   *
   * @param userId of the user involved
   * @param pageable of the requested page
   * @return Page of TransactionDto
   * @throws ResourceNotFoundException if user not found
   */
  Page<TransactionDto> getFromUser(int userId, Pageable pageable) throws ResourceNotFoundException;

  /**
   * Request a transaction between users according to the data send in the form of DTO with both
   * users id, amount and description of the transaction.
   * Both user will be credited or debited and the transaction saved in the database.
   *
   * @param request of the transaction
   * @return TransactionDto of the performed transaction
   * @throws ResourceNotFoundException if user not found
   * @throws InsufficientProvisionException if provision insufficient to perform the transaction
   */
  TransactionDto requestTransaction(TransactionDto request)
      throws ResourceNotFoundException, ForbiddenOperationException;

  /**
   * Calculate from the amount of the transaction the fare that will be added to the total to debit
   * from the emitter.
   *
   * @param amount of the transaction
   * @return fare to apply
   */
  BigDecimal calculateFare(BigDecimal amount);

  /**
   * Clear from a transaction the record of a user as emitter or receiver by setting the field to
   * null and updated the transaction in the database. If the transaction don't have any emitter and
   * receiver left, then it will be deleted from the database.
   *
   * @param transaction to clear
   * @param user to delete
   */
  void clearTransactionForUser(Transaction transaction, User user);
}