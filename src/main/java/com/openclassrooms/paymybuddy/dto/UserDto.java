package com.openclassrooms.paymybuddy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
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
public class UserDto {

  private int userId;
  @NotBlank(message = "Firstname is mandatory")
  private String firstname;
  @NotBlank(message = "Lastname is mandatory")
  private String lastname;
  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email should be a valid email address")
  private String email;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotBlank(message = "Password is mandatory", groups = SubsciptionValidation.class)
  @Size(min = 8, message = "Password should have at least 8 characters")
  private String password;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BigDecimal wallet;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonFormat(pattern = "yyyy-MM-dd' at 'HH:mm")
  private LocalDateTime registrationDate;

  /**
   * Interface definition of Subscription validation to validate subscribe only constraints.
   */
  public interface SubsciptionValidation extends Default {}

}
