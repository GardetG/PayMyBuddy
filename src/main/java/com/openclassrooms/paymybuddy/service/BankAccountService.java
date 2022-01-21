package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing user's bank accounts.
 */
@Service
public interface BankAccountService {

  /**
   * Get a page of the list of all bank accounts of the user in the form of DTO.
   * The pagination doesn't handle sorting.
   *
   * @param userId of the user
   * @param pageable for the requested page
   * @return Page of BankAccountDto
   * @throws ResourceNotFoundException if user not found
   */
  Page<BankAccountDto> getAllFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException;

  /**
   * Add a bank account to the user according to the data sent in the form of a DTO and persist it
   * in the database.
   *
   * @param userId of the user
   * @param account data of the bank account to create
   * @return BankAccountDto of the created bank account
   * @throws ResourceNotFoundException if user not found
   * @throws ResourceAlreadyExistsException if bank account already exists
   */
  BankAccountDto addToUser(int userId, BankAccountDto account)
      throws ResourceNotFoundException, ResourceAlreadyExistsException;

  /**
   * Remove a bank account from the user by its id and delete it from the database.
   *
   * @param userId of the user
   * @param id of the bank account
   * @throws ResourceNotFoundException if user or account not found
   */
  void removeFromUser(int userId, int id) throws ResourceNotFoundException;
}
