package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserRegistrationDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.UserService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
   * Handle HTTP GET request on all user's information.
   *
   * @param pageable for user's information page
   * @return HTTP 200 with user's information page
   */
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users")
  public ResponseEntity<Page<UserInfoDto>> getInfoById(Pageable pageable) {

    LOGGER.info("Request: Get all users information");
    Page<UserInfoDto> userInfo = userService.getAll(pageable);

    LOGGER.info("Response: All users information sent");
    return ResponseEntity.ok(userInfo);
  }


  /**
   * Handle HTTP GET request on user's information by id.
   *
   * @param id of the user
   * @return HTTP 200 Response with user's information
   * @throws ResourceNotFoundException when user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/users/{id}")
  public ResponseEntity<UserInfoDto> getInfoById(@PathVariable int id)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} information", id);
    UserInfoDto userInfo = userService.getById(id);

    LOGGER.info("Response: user information sent");
    return ResponseEntity.ok(userInfo);
  }

  /**
   * Handle HTTP POST user registration.
   *
   * @param userSubscription of the registering user
   * @return HTTP 201 Response with registered user's information
   * @throws EmailAlreadyExistsException when requesting email already exists
   */
  @PostMapping("/register")
  public ResponseEntity<UserInfoDto> register(
      @Valid @RequestBody UserRegistrationDto userSubscription)
      throws EmailAlreadyExistsException {

    LOGGER.info("Request: Registering user");
    UserInfoDto userInfo = userService.register(userSubscription);

    LOGGER.info("Response: user successfully registered");
    return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
  }

  /**
   * Handle HTTP PUT update user's information..
   *
   * @param userUpdate user's information to update
   * @return HTTP 201 Response with updated user's information
   * @throws EmailAlreadyExistsException when updating whith an already existing email
   * @throws ResourceNotFoundException when user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #userUpdate.userId == authentication.principal.userId")
  @PutMapping("/users")
  public ResponseEntity<UserInfoDto> update(@Valid @RequestBody UserInfoDto userUpdate)
      throws EmailAlreadyExistsException, ResourceNotFoundException {

    LOGGER.info("Request: Update user {}", userUpdate.getUserId());
    UserInfoDto userInfo = userService.update(userUpdate);

    LOGGER.info("Response: user successfully updated");
    return ResponseEntity.ok(userInfo);
  }

  /**
   * Handle HTTP DELETE request on an user by id.
   *
   * @param id of user to delete
   * @return HTTP 204
   * @throws ResourceNotFoundException when user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable int id)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Delete user {}", id);
    userService.deleteById(id);

    LOGGER.info("Response: user deleted");
    return ResponseEntity.noContent().build();

  }
}
