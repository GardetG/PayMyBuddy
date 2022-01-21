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

  /**
   * Get a page of the list of all users in the form of DTO.
   *
   * @param pageable of the requested page
   * @return Page of UserDto
   */
  Page<UserDto> getAll(Pageable pageable);

  /**
   * Get a user DTO by id.
   *
   * @param id of user
   * @return UserDto
   * @throws ResourceNotFoundException if user not found
   */
  UserDto getById(int id) throws ResourceNotFoundException;

  /**
   * Handle user registration from the data provide in the form of DTO with firstname, lastname,
   * email and password. Password will be encoded and email will be checked to ensure email
   * uniqueness. The new user is saved in the database.
   *
   * @param user to register
   * @return UserDto of the registered user
   * @throws ResourceAlreadyExistsException if email already exists
   */
  UserDto register(UserDto user) throws ResourceAlreadyExistsException;

  /**
   * Handle user update from the data provide in the form of DTO. If email has benn changed, it
   * will be checked. If password field is left blank or null it won't be updated, else the new
   * password will be encoded and the user updated in the database.
   *
   * @param user update
   * @return UserDto of the updated user
   * @throws ResourceNotFoundException if user not found
   * @throws ResourceAlreadyExistsException if email already exists
   */
  UserDto update(UserDto user) throws ResourceNotFoundException,
      ResourceAlreadyExistsException;

  /**
   * Set user enabling to authenticate.
   *
   * @param id of the user
   * @param enable to update
   * @throws ResourceNotFoundException if user not found
   */
  void setAccountEnabling(int id, boolean enable) throws ResourceNotFoundException;

  /**
   * Delete a user by checking if wallet amount is null and clearing connections with other users.
   * All onUserDeletion() method of observer service will be called before deleting the user from
   * the database.
   *
   * @param id of the user
   * @throws ResourceNotFoundException if user not found
   * @throws ForbiddenOperationException if user wallet isn't empty
   */
  void deleteById(int id) throws ResourceNotFoundException, ForbiddenOperationException;

  /**
   * Return a User entity if it exists by id.
   *
   * @param id of the user
   * @return User
   * @throws ResourceNotFoundException if user not found
   */
  User retrieveEntity(int id) throws ResourceNotFoundException;

  /**
   * Return a User entity if it exists by email.
   *
   * @param email of the user
   * @return User
   * @throws ResourceNotFoundException if user not found
   */
  User retrieveEntity(String email) throws ResourceNotFoundException;

  /**
   * Persist a user in the database if it already exists.
   *
   * @param user to persist
   * @return User
   * @throws ResourceNotFoundException if user not exists
   */
  User saveEntity(User user) throws ResourceNotFoundException;

  /**
   * Add a service implementing the UserDeletionObserver interface to the list of observers.
   *
   * @param observer to add
   */
  void userDeletionSubscribe(UserDeletionObserver observer);
}
