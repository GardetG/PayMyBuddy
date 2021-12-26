package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class for users.
 */
@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserInfoDto getInfoById(int id) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new ResourceNotFoundException("This user is not found");
    }

    UserInfoDto userDto = new UserInfoDto();
    userDto.setUserId(user.get().getUserId());
    userDto.setFirstname(user.get().getFirstname());
    userDto.setLastname(user.get().getLastname());
    userDto.setEmail(user.get().getEmail());
    userDto.setWallet(user.get().getWallet());
    return userDto;
  }
}
