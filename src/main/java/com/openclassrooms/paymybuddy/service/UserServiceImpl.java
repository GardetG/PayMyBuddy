package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ErrorMessage;
import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.UserMapper;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing users.
 */
@Service
public class UserServiceImpl implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Page<UserDto> getAll(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(UserMapper::toInfoDto);
  }

  @Override
  public UserDto getById(int id) throws ResourceNotFoundException {
    User user = getUserById(id);
    return UserMapper.toInfoDto(user);
  }

  @Transactional
  @Override
  public UserDto register(UserDto user) throws ResourceAlreadyExistsException {
    checkEmail(user.getEmail());

    User userToCreate = UserMapper.toModel(user);
    userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));

    return UserMapper.toInfoDto(userRepository.save(userToCreate));
  }

  @Transactional
  @Override
  public UserDto update(UserDto userUpdate) throws ResourceNotFoundException,
      ResourceAlreadyExistsException {
    User user = getUserById(userUpdate.getUserId());

    if (!userUpdate.getEmail().equals(user.getEmail())) {
      checkEmail(userUpdate.getEmail());
      user.setEmail(userUpdate.getEmail());
    }
    if (userUpdate.getPassword() != null && !userUpdate.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
    }
    user.setFirstname(userUpdate.getFirstname());
    user.setLastname(userUpdate.getLastname());


    return UserMapper.toInfoDto(userRepository.save(user));
  }

  @Transactional
  @Override
  public void deleteById(int id) throws ResourceNotFoundException, ForbiddenOperationException {
    User user = getUserById(id);
    if (user.getBalance().signum() != 0) {
      LOGGER.error("The user {} can't delete account if wallet not empty", id);
      throw new ForbiddenOperationException("The user can't delete account if wallet not empty");
    }

    user.getConnections().forEach(c -> {
      try {
        user.removeConnection(c);
      } catch (ResourceNotFoundException e) {
        LOGGER.error("Can't find connection to remove");
      }
    });

    userRepository.delete(user);
  }

  /**
   * Return a User by the id or throw an exception.
   *
   * @param userId of the user
   * @return User
   * @throws ResourceNotFoundException if user not found
   */
  public User getUserById(int userId) throws ResourceNotFoundException {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          LOGGER.error(ErrorMessage.USER_NOT_FOUND + ": {}", userId);
          return new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
        });
  }

  private void checkEmail(String email) throws ResourceAlreadyExistsException {
    if (userRepository.existsByEmail(email)) {
      LOGGER.error(ErrorMessage.EMAIL_ALREADY_EXIST + ": {}", email);
      throw new ResourceAlreadyExistsException(ErrorMessage.EMAIL_ALREADY_EXIST);
    }
  }

}
