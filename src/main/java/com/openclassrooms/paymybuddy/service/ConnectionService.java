package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing user's connections with other users.
 */
@Service
public interface ConnectionService {

  List<ConnectionDto> getAllFromUser(int userId) throws ResourceNotFoundException;

  ConnectionDto addToUser(int userId, ConnectionDto connection)
      throws ResourceNotFoundException, ResourceAlreadyExistsException, ForbiddenOperationException;

  void removeFromUser(int userId, int id) throws ResourceNotFoundException;

}
