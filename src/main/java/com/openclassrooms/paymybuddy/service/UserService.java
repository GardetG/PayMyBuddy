package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing users.
 */
@Service
public interface UserService {

  Page<UserDto> getAll(Pageable pageable);

  UserDto getById(int id) throws ResourceNotFoundException;

  UserDto register(UserDto user) throws ResourceAlreadyExistsException;

  UserDto update(UserDto user) throws ResourceNotFoundException,
      ResourceAlreadyExistsException;

  void deleteById(int id) throws ResourceNotFoundException, ForbiddenOperationException;

  User retrieveEntity(int id) throws ResourceNotFoundException;

  User retrieveEntity(String email) throws ResourceNotFoundException;

  User saveEntity(User user) throws ResourceNotFoundException;

  void userDeletionSubscribe(UserDeletionObserver observer);
}
