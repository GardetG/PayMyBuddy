package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.BankAccountService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Controller Class for managing user bank account.
 */
@Controller
@Validated
public class BankAccountController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountController.class);

  @Autowired
  BankAccountService bankAccountService;

}
