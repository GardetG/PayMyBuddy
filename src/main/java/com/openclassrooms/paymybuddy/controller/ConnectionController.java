package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller Class for managing user connections.
 */
@Controller
@Validated
public class ConnectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionController.class);

  @Autowired
  ConnectionService connectionService;

  /**
   * Handle HTTP GET request on user's connections by id.
   *
   * @param id of the user
   * @return HTTP 200 Response with connections list
   * @throws ResourceNotFoundException when user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/users/{id}/connections")
  public ResponseEntity<List<ConnectionDto>> getAllFromUser(@PathVariable int id)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} connections", id);
    List<ConnectionDto> bankAccounts = connectionService.getAllFromUser(id);

    LOGGER.info("Response: List of user connectionss sent");
    return ResponseEntity.ok(bankAccounts);
  }

  /**
   * Handle HTTP DELETE request on a user connections by id.
   *
   * @param id of user
   * @param connectionId of the connection
   * @return HTTP 204
   * @throws ResourceNotFoundException when user or connection not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @DeleteMapping("/users/{id}/connections/{connectionId}")
  public ResponseEntity<Void> removeFromUser(@PathVariable int id, @PathVariable int connectionId)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Delete user {} connection {}", id, connectionId);
    connectionService.removeFromUser(id, connectionId);

    LOGGER.info("Response: user bank account deleted");
    return ResponseEntity.noContent().build();

  }

}
