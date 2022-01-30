package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.TransactionService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Class for managing transaction between users.
 */
@Controller
@Validated
public class TransactionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  private TransactionService transactionService;

  /**
   * Handle HTTP GET request on all transactions. Reserved to admin
   *
   * @param pageable of the requested page
   * @return HTTP 200 with transactions page
   */
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/transactions")
  public ResponseEntity<Page<TransactionDto>> getInfoById(Pageable pageable) {

    LOGGER.info("Request: Get all transactions");
    Page<TransactionDto> transactionsDto = transactionService.getAll(pageable);

    LOGGER.info("Response: Page of transactions sent");
    return ResponseEntity.ok(transactionsDto);
  }


  /**
   * Handle HTTP GET request on all transactions of a user.

   * @param id of the user
   * @param pageable of the requested page
   * @return HTTP 200 Response with user's transactions page
   * @throws ResourceNotFoundException if user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/transactions/user")
  public ResponseEntity<Page<TransactionDto>> getInfoById(@RequestParam int id, Pageable pageable)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} transactions", id);
    Page<TransactionDto> transactionsDto = transactionService.getFromUser(id, pageable);

    LOGGER.info("Response: Page of user transactions sent");
    return ResponseEntity.ok(transactionsDto);
  }

  /**
   * Handle HTTP POST request for a transaction.
   *
   * @param request of the transfer
   * @return HTTP Response 201 with transaction performed
   * @throws InsufficientProvisionException if provision insufficient to perform transaction
   * @throws ResourceNotFoundException if user not found
   */
  @PreAuthorize("#request.emitterId == authentication.principal.userId")
  @PostMapping("/transactions")
  public ResponseEntity<TransactionDto> request(@Valid @RequestBody TransactionDto request)
      throws ForbiddenOperationException, ResourceNotFoundException {

    LOGGER.info("Request: Transaction from user {} to user {}", request.getEmitterId(),
        request.getReceiverId());
    TransactionDto requestResponse = transactionService.requestTransaction(request);

    LOGGER.info("Response: Transaction successfully performed");
    return ResponseEntity.status(HttpStatus.CREATED).body(requestResponse);
  }

}
