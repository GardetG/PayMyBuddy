package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing user's connections with other users.
 */
@Service
public interface ConnectionService {

  /**
   * Get a page of the list of all connections of the user in the form of DTO.
   * The pagination doesn't handle sorting.
   *
   * @param userId of the user
   * @param pageable for the requested page
   * @return Page of ConnectionDto
   * @throws ResourceNotFoundException if user not found
   */
  Page<ConnectionDto> getAllFromUser(int userId, Pageable pageable)
      throws ResourceNotFoundException;

  /**
   * Add a bank account to the user according to the email sent in a DTO and persist it in the
   * database.
   *
   * @param userId of the user
   * @param connection DTO of the connection to add
   * @return ConnectionDto of the added connection
   * @throws ResourceNotFoundException if user or connection not found
   * @throws ResourceAlreadyExistsException if connection already exists
   * @throws ForbiddenOperationException if user try to add himself as connection
   */
  ConnectionDto addToUser(int userId, ConnectionDto connection)
      throws ResourceNotFoundException, ResourceAlreadyExistsException, ForbiddenOperationException;

  /**
   * Remove a connection from the user by its id and delete it from the database.
   *
   * @param userId of the user
   * @param id of the connection
   * @throws ResourceNotFoundException if user or connection not found
   */
  void removeFromUser(int userId, int id) throws ResourceNotFoundException;

}
