package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.UserService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
   * Handle HTTP GET request on all user. Reserved to admin.
   *
   * @param pageable for requested page
   * @return HTTP 200 with users page
   */
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users")
  public ResponseEntity<Page<UserDto>> getInfoById(Pageable pageable) {

    LOGGER.info("Request: Get all users information");
    Page<UserDto> userInfo = userService.getAll(pageable);

    LOGGER.info("Response: All users information sent");
    return ResponseEntity.ok(userInfo);
  }


  /**
   * Handle HTTP GET request on user's information by id.
   *
   * @param id of the user
   * @return HTTP 200 Response with user
   * @throws ResourceNotFoundException if user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/users/{id}")
  public ResponseEntity<UserDto> getInfoById(@PathVariable int id)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} information", id);
    UserDto userInfo = userService.getById(id);

    LOGGER.info("Response: user information sent");
    return ResponseEntity.ok(userInfo);
  }

  /**
   * Handle HTTP PUT update user's information.
   *
   * @param userUpdate user's information to update
   * @return HTTP 201 Response with updated user's information
   * @throws ResourceAlreadyExistsException if requested email already exists
   * @throws ResourceNotFoundException if user not found
   */
  @PreAuthorize("#userUpdate.userId == authentication.principal.userId")
  @PutMapping("/users")
  public ResponseEntity<UserDto> update(@Valid @RequestBody UserDto userUpdate)
      throws ResourceAlreadyExistsException, ResourceNotFoundException {

    LOGGER.info("Request: Update user {}", userUpdate.getUserId());
    UserDto userInfo = userService.update(userUpdate);

    LOGGER.info("Response: user successfully updated");
    return ResponseEntity.ok(userInfo);
  }

  /**
   * Handle HTTP DELETE request on a user by id.
   *
   * @param id of user to delete
   * @return HTTP 204
   * @throws ResourceNotFoundException if user not found
   * @throws ForbiddenOperationException if user wallet not empty
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable int id)
      throws ResourceNotFoundException, ForbiddenOperationException {

    LOGGER.info("Request: Delete user {}", id);
    userService.deleteById(id);

    LOGGER.info("Response: user deleted");
    return ResponseEntity.noContent().build();
  }
}
