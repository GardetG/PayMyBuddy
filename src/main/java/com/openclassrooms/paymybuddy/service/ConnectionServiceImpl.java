package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ErrorMessage;
import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.ConnectionMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing user's connections with other users.
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionServiceImpl.class);

  @Autowired
  UserRepository userRepository;

  @Override
  public List<ConnectionDto> getAllFromUser(int userId) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(ErrorMessage.USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    return user.get().getConnections().stream()
        .map(ConnectionMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public ConnectionDto addToUser(int userId, ConnectionDto connection)
      throws ResourceNotFoundException, ResourceAlreadyExistsException {
    return null;
  }

  @Override
  public void removeFromUser(int userId, int id) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(ErrorMessage.USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    User connection = user.get().getConnections().stream()
        .filter(c -> c.getUserId() == id)
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("This connection is not found"));

    user.get().removeConnection(connection);
    userRepository.save(user.get());
  }
}
