package com.openclassrooms.paymybuddy.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO Class for retrieving bank account data like title, masked iban and bic.
 */
@Getter
@AllArgsConstructor
public class BankAccountDto {

  private int bankAccountId;
  @NotBlank(message = "Title is mandatory")
  private String title;
  @NotBlank(message = "IBAN is mandatory")
  @Size(min = 14, max = 34, message = "IBAN should have between 14 and 34 characters")
  private String iban;
  @NotBlank(message = "BIC is mandatory")
  @Size(min = 8, max = 11, message = "BIC should have between 8 and 11 characters")
  private String bic;

}
