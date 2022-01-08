package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.utils.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller Class for managing authentication and subscription.
 */
@Controller
@Validated
public class AuthenticationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

  @Autowired
  UserService userService;

  /**
   * Handle HTTP POST user registration.
   *
   * @param subscriptionDto of the registering user
   * @return HTTP 201 Response with registered user's information
   * @throws ResourceAlreadyExistsException when requested email already exists
   */
  @PostMapping("/register")
  public ResponseEntity<UserDto> register(
      @Validated(UserDto.SubsciptionValidation.class) @RequestBody UserDto subscriptionDto)
      throws ResourceAlreadyExistsException {

    LOGGER.info("Request: Registering user");
    UserDto userInfo = userService.register(subscriptionDto);

    LOGGER.info("Response: User successfully registered");
    return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
  }

  /**
   * Handle HTTP GET user login.
   *
   * @param myUser user authenticate
   * @return User information
   */
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @GetMapping("/login")
  public ResponseEntity<UserDto> login(@AuthenticationPrincipal User myUser) {

    LOGGER.info("Request: Login user {}", myUser.getUserId());
    UserDto userInfo = UserMapper.toInfoDto(myUser);

    LOGGER.info("Response: User information sent");
    return ResponseEntity.ok(userInfo);
  }
}
