package com.openclassrooms.paymybuddy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO Class for bank transfer with date and amount of the transaction and user and bank account
 * information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankTransferDto {

  private int userId;
  private int bankAccountId;
  @DecimalMin(value = "0.00", inclusive = false, message = "Amount can't be negative")
  @Digits(integer = 10, fraction = 2, message = "Amount can't have more than 2 decimals")
  private BigDecimal amount;
  private boolean isIncome;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonFormat(pattern = "yyyy-MM-dd' at 'HH:mm")
  private LocalDateTime date;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String firstname;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String lastname;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String title;
}
