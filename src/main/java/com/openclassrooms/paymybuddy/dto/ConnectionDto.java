package com.openclassrooms.paymybuddy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO Class for user connection.
 */
@Getter
@AllArgsConstructor
public class ConnectionDto {

  private int connectionId;
  private String firstname;
  private String lastname;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email should be a valid email address")
  private String email;

}
