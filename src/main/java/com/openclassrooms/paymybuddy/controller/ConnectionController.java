package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.ConnectionService;
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
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller Class for managing user connections with other users.
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
   * @param pageable of the requested page
   * @return HTTP 200 Response with connections page
   * @throws ResourceNotFoundException if user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/users/{id}/connections")
  public ResponseEntity<Page<ConnectionDto>> getAllFromUser(@PathVariable int id, Pageable pageable)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} connections", id);
    Page<ConnectionDto> bankAccounts = connectionService.getAllFromUser(id, pageable);

    LOGGER.info("Response: Page of user connections sent");
    return ResponseEntity.ok(bankAccounts);
  }

  /**
   * Handle HTTP POST request on user connections.
   *
   * @param id of user
   * @param connection to add
   * @return HTTP 201
   * @throws ResourceNotFoundException if user or requested connection not found
   */
  @PreAuthorize("#id == authentication.principal.userId")
  @PostMapping("/users/{id}/connections")
  public ResponseEntity<ConnectionDto> addToUser(@PathVariable int id, @Valid @RequestBody
      ConnectionDto connection)
      throws ResourceNotFoundException, ResourceAlreadyExistsException,
      ForbiddenOperationException {

    LOGGER.info("Request: Add user {} new connection", id);
    ConnectionDto connectionAdded = connectionService.addToUser(id, connection);

    LOGGER.info("Response: User connection with {} added", connectionAdded.getConnectionId());
    return ResponseEntity.status(HttpStatus.CREATED).body(connectionAdded);

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

    LOGGER.info("Request: Delete user {} connection with {}", id, connectionId);
    connectionService.removeFromUser(id, connectionId);

    LOGGER.info("Response: User connection deleted");
    return ResponseEntity.noContent().build();

  }

}
