package com.openclassrooms.paymybuddy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO Class for transaction.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

  private int emitterId;
  private int receiverId;
  @NotBlank(message = "Description is mandatory")
  private String description;
  @DecimalMin(value = "0.00", inclusive = false, message = "Amount can't be negative")
  @Digits(integer = 10, fraction = 2, message = "Amount can't have more than 2 decimals")
  private BigDecimal amount;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime date;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String emitterFirstname;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String emitterLastName;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String receiverFirstname;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String receiverLastname;
}