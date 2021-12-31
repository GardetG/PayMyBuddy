package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserSubscriptionDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.UserService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
   * Handle HTTP GET request on user's information by id.
   *
   * @param id of the user
   * @return HTTP 200 Response with user's information
   * @throws ResourceNotFoundException when user not found
   */
  @GetMapping("/users/{id}")
  public ResponseEntity<UserInfoDto> getInfoById(@PathVariable int id)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} information", id);
    UserInfoDto userInfo = userService.getInfoById(id);

    LOGGER.info("Response: user information sent");
    return ResponseEntity.ok(userInfo);
  }

  /**
   * Handle HTTP POST user subscription.
   *
   * @param userSubscription of the subscribing user
   * @return HTTP 201 Response with subscribed user's information
   * @throws EmailAlreadyExistsException when requesting email already exists
   */
  @PostMapping("/subscribe")
  public ResponseEntity<UserInfoDto> subscribe(
      @Valid @RequestBody UserSubscriptionDto userSubscription)
      throws EmailAlreadyExistsException {

    LOGGER.info("Request: Subscribing user");
    UserInfoDto userInfo = userService.subscribe(userSubscription);

    LOGGER.info("Response: user successfully subscribed");
    return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
  }

  /**
   * Handle HTTP PUT update user's information..
   *
   * @return HTTP 201 Response with updated user's information
   */
  @PutMapping("/users")
  public ResponseEntity<UserInfoDto> update(@Valid @RequestBody UserInfoDto userUpdate)
      throws EmailAlreadyExistsException, ResourceNotFoundException {

    LOGGER.info("Request: Update user {}", userUpdate.getUserId());
    UserInfoDto userInfo = userService.update(userUpdate);

    LOGGER.info("Response: user successfully updated");
    return ResponseEntity.ok(userInfo);
  }
}
