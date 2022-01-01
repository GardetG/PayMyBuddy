package com.openclassrooms.paymybuddy.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
public class BankAccountDto {

  private int bankAccountId;
  @NotBlank(message = "Title is mandatory")
  private String title;
  @NotBlank(message = "IBAN is mandatory")
  @Size(min = 14, max = 34, message = "Password should have between 14 and 34 characters")
  private String iban;
  @NotBlank(message = "BIC is mandatory")
  @Size(min = 8, max = 11, message = "Password should have between 8 and 11 characters")
  private String bic;
  private BigDecimal balance;
  private int userId;

}
