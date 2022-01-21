package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.UserMapper;
import java.util.ArrayList;
import java.util.List;
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
  private final List<UserDeletionObserver> observers = new ArrayList<>();

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * {@inheritDoc}
   */
  @Override
  public Page<UserDto> getAll(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(UserMapper::toInfoDto);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserDto getById(int id) throws ResourceNotFoundException {
    User user = retrieveEntity(id);
    return UserMapper.toInfoDto(user);
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public UserDto register(UserDto user) throws ResourceAlreadyExistsException {
    checkEmail(user.getEmail());

    User userToCreate = UserMapper.toModel(user);
    userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));

    return UserMapper.toInfoDto(userRepository.save(userToCreate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public UserDto update(UserDto userUpdate) throws ResourceNotFoundException,
      ResourceAlreadyExistsException {
    User user = retrieveEntity(userUpdate.getUserId());

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

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void setAccountEnabling(int id, boolean enable) throws ResourceNotFoundException {
    User user = retrieveEntity(id);
    user.setEnabled(enable);
    userRepository.save(user);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void deleteById(int id) throws ResourceNotFoundException, ForbiddenOperationException {
    User user = retrieveEntity(id);
    if (user.getBalance().signum() != 0) {
      LOGGER.error("The user {} can't delete account if wallet not empty", id);
      throw new ForbiddenOperationException("The user can't delete account if wallet not empty");
    }
    user.clearConnection();
    observers.forEach(observer -> observer.onUserDeletion(user));

    userRepository.delete(user);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public User retrieveEntity(int userId) throws ResourceNotFoundException {
    return userRepository.findById(userId)
        .orElseThrow(() ->  logAndThrow(String.valueOf(userId)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public User retrieveEntity(String email) throws ResourceNotFoundException {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> logAndThrow(email));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public User saveEntity(User user) throws ResourceNotFoundException {
    if (!userRepository.existsById(user.getUserId())) {
      throw logAndThrow(String.valueOf(user.getUserId()));
    }
    return userRepository.save(user);
  }

  private ResourceNotFoundException logAndThrow(String id) {
    LOGGER.error("This user is not found: {}", id);
    return new ResourceNotFoundException("This user is not found");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void userDeletionSubscribe(UserDeletionObserver observer) {
    observers.add(observer);
  }

  private void checkEmail(String email) throws ResourceAlreadyExistsException {
    if (userRepository.existsByEmail(email)) {
      LOGGER.error("This email already exists: {}", email);
      throw new ResourceAlreadyExistsException("This email already exists");
    }
  }

}
