package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.BankTransferService;
import com.openclassrooms.paymybuddy.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Controller Class for managing transaction between users.
 */
@Controller
@Validated
public class TransactionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  TransactionService transactionService;

}
