package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller Class for managing user information.
 */
@Controller
@Validated
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  @Autowired
  UserService userService;

  /**
   * Handle HTTP GET request on user.
   *
   * @return HTTP 200 Response with user's information
   */
  @GetMapping("/users/{id}")
  public ResponseEntity<UserInfoDto> getInfoById(@PathVariable int id)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} information", id);
    UserInfoDto userInfo = userService.getInfoById(id);

    LOGGER.info("Response: user information sent");
    return ResponseEntity.ok(userInfo);
  }
}
