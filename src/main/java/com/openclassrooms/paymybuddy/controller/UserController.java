package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Controller Class for managing user information.
 */
@Controller
@Validated
public class UserController {

  @Autowired
  UserService userService;

}
