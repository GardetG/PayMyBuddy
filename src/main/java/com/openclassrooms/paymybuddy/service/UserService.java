package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserSubscriptionDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service interface for users.
 */
@Service
public interface UserService {

  UserInfoDto getInfoById(int id) throws ResourceNotFoundException;

  UserInfoDto subscribe(UserSubscriptionDto user) throws EmailAlreadyExistsException;

}
