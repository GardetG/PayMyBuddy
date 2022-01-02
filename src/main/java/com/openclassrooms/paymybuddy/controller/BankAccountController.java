package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.BankAccountService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller Class for managing user bank account.
 */
@Controller
@Validated
public class BankAccountController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountController.class);

  @Autowired
  BankAccountService bankAccountService;

  /**
   * Handle HTTP GET request on user's bank accounts by id.
   *
   * @param id of the user
   * @return HTTP 200 Response with bank accounts list
   * @throws ResourceNotFoundException when user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/users/{id}/bankaccounts")
  public ResponseEntity<List<BankAccountDto>> getByUserId(@PathVariable int id)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} bank accounts", id);
    List<BankAccountDto> bankAccounts = bankAccountService.getAllByUserId(id);

    LOGGER.info("Response: List of user bank accounts sent");
    return ResponseEntity.ok(bankAccounts);
  }

  /**
   * Handle HTTP DELETE request on an user bank account by id.
   *
   * @param id of user
   * @param accountId of the account
   * @return HTTP 204
   * @throws ResourceNotFoundException when user or account not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @DeleteMapping("/users/{id}/bankaccounts/{accountId}")
  public ResponseEntity<Void> deleteUser(@PathVariable int id, @PathVariable int accountId)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Delete user {} bank account {}", id, accountId);
    bankAccountService.deleteById(id, accountId);

    LOGGER.info("Response: user bank account deleted");
    return ResponseEntity.noContent().build();

  }

}
