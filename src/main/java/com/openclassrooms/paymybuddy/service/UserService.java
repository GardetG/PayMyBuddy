package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserRegistrationDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service interface for users.
 */
@Service
public interface UserService {

  UserInfoDto getInfoById(int id) throws ResourceNotFoundException;

  UserInfoDto register(UserRegistrationDto user) throws EmailAlreadyExistsException;

  UserInfoDto update(UserInfoDto user) throws ResourceNotFoundException,
      EmailAlreadyExistsException;

  void deleteById(int id) throws ResourceNotFoundException;

}
