package com.openclassrooms.paymybuddy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * DTO Class for transaction between user with emitter and receiver, amount, date, and description.
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
  @DecimalMin(value = "1.00", message = "Amount must be greater then 1.00")
  @Digits(integer = 10, fraction = 2, message = "Amount can't have more than 2 decimals")
  private BigDecimal amount;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonFormat(pattern = "yyyy-MM-dd' at 'HH:mm")
  private LocalDateTime date;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String emitterFirstname;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String emitterLastname;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String receiverFirstname;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String receiverLastname;
}