package com.openclassrooms.paymybuddy.utils;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.model.User;

/**
 * Mapper utility class for Connection.
 */
public class ConnectionMapper {

  private ConnectionMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a User into a ConnectionDto.
   *
   * @param user to map
   * @return ConnectionDto
   */
  public static ConnectionDto toDto(User user) {
    return new ConnectionDto(
        user.getUserId(),
        user.getFirstname(),
        user.getLastname(),
        user.getEmail()
    );
  }
}
