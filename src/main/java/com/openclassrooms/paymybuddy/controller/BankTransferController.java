package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.BankAccountService;
import com.openclassrooms.paymybuddy.service.BankTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Controller Class for managing bank transfer between user and bank account.
 */
@Controller
@Validated
public class BankTransferController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankTransferController.class);

  @Autowired
  BankTransferService bankTransferService;

}
