package com.openclassrooms.paymybuddy.model;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract Class for comptable entity with a balance to handle financial operations.
 */
@MappedSuperclass
public abstract class ComptableEntity {

  @Column(name = "balance")
  @Getter
  @Setter
  private BigDecimal balance = ApplicationValue.INITIAL_USER_BALANCE;

}
