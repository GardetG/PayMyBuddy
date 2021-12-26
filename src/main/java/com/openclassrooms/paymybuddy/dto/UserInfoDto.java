package com.openclassrooms.paymybuddy.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO Class for retrieving user data like id, firstname, lastname, email and wallet.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

  private int userId;
  private String firstname;
  private String lastname;
  private String email;
  private BigDecimal wallet;

}
