package com.openclassrooms.paymybuddy.utils;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import java.time.LocalDateTime;

/**
 * Mapper utility class for user.
 */
public class UserMapper {

  private UserMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a user into an userInfoDto.
   *

   * @param user to map
   * @return userDto
   */
  public static UserDto toInfoDto(User user) {
    UserDto userDto = new UserDto();
    userDto.setUserId(user.getUserId());
    userDto.setFirstname(user.getFirstname());
    userDto.setLastname(user.getLastname());
    userDto.setEmail(user.getEmail());
    userDto.setWallet(user.getBalance());
    userDto.setRegistrationDate(user.getRegistrationDate());
    return userDto;
  }

  /**
   * Map a userSubscriptionDto into a new user.
   *

   * @param subscription to map
   * @return user to create
   */
  public static User toModel(UserDto subscription) {
    return new User(
        subscription.getFirstname(),
        subscription.getLastname(),
        subscription.getEmail(),
        subscription.getPassword(),
        Role.USER,
        LocalDateTime.now()
    );
  }
}
