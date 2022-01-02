package com.openclassrooms.paymybuddy.utils;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserRegistrationDto;
import com.openclassrooms.paymybuddy.model.User;
import java.math.BigDecimal;

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
  public static UserInfoDto toInfoDto(User user) {
    UserInfoDto userDto = new UserInfoDto();
    userDto.setUserId(user.getUserId());
    userDto.setFirstname(user.getFirstname());
    userDto.setLastname(user.getLastname());
    userDto.setEmail(user.getEmail());
    userDto.setWallet(user.getWallet());
    userDto.setRole(user.getRole().getName());
    return userDto;
  }

  /**
   * Map a userSubscriptionDto into a new user.
   *

   * @param subscription to map
   * @return user to create
   */
  public static User toModel(UserRegistrationDto subscription) {
    User user = new User();
    user.setFirstname(subscription.getFirstname());
    user.setLastname(subscription.getLastname());
    user.setEmail(subscription.getEmail());
    user.setWallet(BigDecimal.ZERO);
    return user;
  }
}
