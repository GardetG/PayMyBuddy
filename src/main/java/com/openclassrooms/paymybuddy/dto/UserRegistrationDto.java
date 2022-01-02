package com.openclassrooms.paymybuddy.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO Class for user subscription with firstname, lastname, email and password.
 */
@Getter
@Setter
@AllArgsConstructor
public class UserRegistrationDto {

  @NotBlank(message = "Firstname is mandatory")
  private String firstname;
  @NotBlank(message = "Lastname is mandatory")
  private String lastname;
  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email should be a valid email address")
  private String email;
  @NotBlank(message = "Password is mandatory")
  @Size(min = 8, message = "Password should have at least 8 characters")
  private String password;

}
