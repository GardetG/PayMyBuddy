package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Class for managing transaction between users.
 */
@Controller
@Validated
public class TransactionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  TransactionService transactionService;

  /**
   * Handle HTTP GET request on all transactions.
   *
   * @param pageable of the requested page
   * @return HTTP 200 with transactions page
   */
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/transactions")
  public ResponseEntity<Page<TransactionDto>> getInfoById(Pageable pageable) {

    LOGGER.info("Request: Get all bank transfers");
    Page<TransactionDto> transactionsDto = transactionService.getAll(pageable);

    LOGGER.info("Response: All bank transfers information sent");
    return ResponseEntity.ok(transactionsDto);
  }


  /**
   * Handle HTTP GET request on all transactions of an user.

   * @param id of the user
   * @return HTTP 200 Response with user's transactions
   * @throws ResourceNotFoundException when user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/transactions/user")
  public ResponseEntity<Page<TransactionDto>> getInfoById(@RequestParam int id, Pageable pageable)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} bank transfers", id);
    Page<TransactionDto> transactionsDto = transactionService.getFromUser(id, pageable);

    LOGGER.info("Response: User bank transfers sent");
    return ResponseEntity.ok(transactionsDto);
  }
  
}
