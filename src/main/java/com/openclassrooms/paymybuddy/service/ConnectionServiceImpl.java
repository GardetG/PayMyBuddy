package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.utils.ConnectionMapper;
import com.openclassrooms.paymybuddy.utils.PaginateCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing user's connections with other users.
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionServiceImpl.class);

  @Autowired
  private UserService userService;

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<ConnectionDto> getAllFromUser(int userId,
                                            Pageable pageable) throws ResourceNotFoundException {
    User user = userService.retrieveEntity(userId);
    Page<User> page = PaginateCollection.paginate(user.getConnections(), pageable);
    return page.map(ConnectionMapper::toDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConnectionDto addToUser(int userId, ConnectionDto connection)
      throws ResourceNotFoundException, ResourceAlreadyExistsException,
      ForbiddenOperationException {

    User user = userService.retrieveEntity(userId);
    if (user.getEmail().equals(connection.getEmail())) {
      LOGGER.error("The user can't add himself as connection");
      throw new ForbiddenOperationException("The user can't add himself as connection");
    }
    User connectionToAdd = userService.retrieveEntity(connection.getEmail());

    user.addConnection(connectionToAdd);
    userService.saveEntity(user);

    return ConnectionMapper.toDto(connectionToAdd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeFromUser(int userId, int id) throws ResourceNotFoundException {
    User user = userService.retrieveEntity(userId);
    User connectionToDelete = user.getConnections().stream()
        .filter(c -> c.getUserId() == id)
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("This connection is not found");
          return new ResourceNotFoundException("This connection is not found");
        });

    user.removeConnection(connectionToDelete);
    userService.saveEntity(user);
  }
}
