package com.openclassrooms.paymybuddy.model;

import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Class for comptable entity with a balance to handle financial operations.
 */
@MappedSuperclass
public abstract class ComptableEntity implements Serializable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ComptableEntity.class);

  protected ComptableEntity() {
    // Private default constructor for hibernate
  }

  protected ComptableEntity(BigDecimal balance) {
    this.balance = balance;
  }

  @Column(name = "balance")
  @Getter
  private BigDecimal balance;

  /**
   * credit balance of the amount. Amount can't be negative.
   *
   * @param amount to credit
   */
  public void credit(BigDecimal amount) {
    if (amount.signum() == -1) {
      LOGGER.error("The amount to credit can't be negative");
      throw new IllegalArgumentException("The amount to credit can't be negative");
    }
    balance = balance.add(amount);
  }

  /**
   * Debit balance of the amount if sufficient provision. Amount can't be negative.
   *
   * @param amount to debit
   * @throws InsufficientProvisionException if insufficient provision
   */
  public void debit(BigDecimal amount) throws InsufficientProvisionException {
    if (amount.signum() == -1) {
      LOGGER.error("The amount to debit can't be negative");
      throw new IllegalArgumentException("The amount to debit can't be negative");
    }
    if (balance.subtract(amount).signum() == -1) {
      LOGGER.error("Insufficient provision to debit the amount");
      throw new InsufficientProvisionException("Insufficient provision to debit the amount");
    }
    balance = balance.subtract(amount);
  }
}
