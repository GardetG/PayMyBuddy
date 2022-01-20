package com.openclassrooms.paymybuddy.dto;

import lombok.Getter;

/**
 * DTO Class for retrieving user identity on login.
 */
@Getter
public class IdentityDto {

  public IdentityDto(int userId, String role) {
    this.userId = userId;
    this.role = role;
  }

  private final int userId;
  private final String role;

}