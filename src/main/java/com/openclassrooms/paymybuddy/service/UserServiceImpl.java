package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ErrorMessage;
import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ForbbidenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.UserMapper;
import java.math.BigDecimal;
import java.util.Optional;
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

  @Override
  public UserDto register(UserDto user) throws ResourceAlreadyExistsException {
    checkEmail(user.getEmail());

    User userToCreate = UserMapper.toModel(user);
    userToCreate.setPassword(passwordEncoder.encode(userToCreate.getPassword()));

    return UserMapper.toInfoDto(userRepository.save(userToCreate));
  }

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

  @Override
  public void deleteById(int id) throws ResourceNotFoundException, ForbbidenOperationException {
    User user = getUserById(id);
    if (!user.getWallet().equals(BigDecimal.ZERO)) {
      LOGGER.error("The user {} can't delete account if wallet not empty", id);
      throw new ForbbidenOperationException("The user can't delete account if wallet not empty");
    }


    userRepository.delete(user);
  }

  private User getUserById(int userId) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(ErrorMessage.USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    return user.get();
  }

  private void checkEmail(String email) throws ResourceAlreadyExistsException {
    if (userRepository.existsByEmail(email)) {
      LOGGER.error(ErrorMessage.EMAIL_ALREADY_EXIST + ": {}", email);
      throw new ResourceAlreadyExistsException(ErrorMessage.EMAIL_ALREADY_EXIST);
    }
  }

}
