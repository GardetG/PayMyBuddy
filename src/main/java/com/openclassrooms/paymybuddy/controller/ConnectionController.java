package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Controller Class for managing user connections.
 */
@Controller
@Validated
public class ConnectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionController.class);

  @Autowired
  ConnectionService connectionService;

}
