package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.BankTransferService;
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
 * Controller Class for managing bank transfer between user and bank account.
 */
@Controller
@Validated
public class BankTransferController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankTransferController.class);

  @Autowired
  BankTransferService bankTransferService;

  /**
   * Handle HTTP GET request on all bank transfers. Reserved to admin.
   *
   * @param pageable of the requested page
   * @return HTTP 200 with bank transfers page
   */
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/banktransfers")
  public ResponseEntity<Page<BankTransferDto>> getInfoById(Pageable pageable) {

    LOGGER.info("Request: Get all bank transfers");
    Page<BankTransferDto> bankTransfersDto = bankTransferService.getAll(pageable);

    LOGGER.info("Response: Page of bank transfers sent");
    return ResponseEntity.ok(bankTransfersDto);
  }


  /**
   * Handle HTTP GET request on all bank transfers of a user.
   *
   * @param id of the user
   * @param pageable of the requested page
   * @return HTTP 200 Response with bank transfers page
   * @throws ResourceNotFoundException if user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/banktransfers/user")
  public ResponseEntity<Page<BankTransferDto>> getInfoById(@RequestParam int id, Pageable pageable)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} bank transfers", id);
    Page<BankTransferDto> bankTransfersDto = bankTransferService.getFromUser(id, pageable);

    LOGGER.info("Response: Page of bank transfers sent");
    return ResponseEntity.ok(bankTransfersDto);
  }

  /**
   * Handle HTTP POST request for a bank transfer.
   *
   * @param request of the transfer
   * @return HTTP Response 201 with transfer performed
   * @throws InsufficientProvisionException if provision insufficient to perform transfer
   * @throws ResourceNotFoundException if account not found
   */
  @PreAuthorize("#request.userId == authentication.principal.userId")
  @PostMapping("/banktransfers")
  public ResponseEntity<BankTransferDto> request(@Valid @RequestBody BankTransferDto request)
      throws ForbiddenOperationException, ResourceNotFoundException {

    LOGGER.info("Request: Bank transfer for user {} with account {}", request.getUserId(),
        request.getBankAccountId());
    BankTransferDto requestResponse = bankTransferService.requestTransfer(request);

    LOGGER.info("Response: Bank transfer successfully performed");
    return ResponseEntity.status(HttpStatus.CREATED).body(requestResponse);
  }
}
