package com.openclassrooms.paymybuddy.model;

import com.openclassrooms.paymybuddy.exception.ExceedingBalanceCeilingException;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Class for comptable entity with a balance to handle financial operations.
 */
@MappedSuperclass
public abstract class ComptableEntity implements Serializable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ComptableEntity.class);

  protected ComptableEntity(BigDecimal maxBalance) {
    this.maxBalance = maxBalance;
  }

  protected ComptableEntity(BigDecimal initialBalance, BigDecimal maxBalance) {
    this.balance = initialBalance;
    this.maxBalance = maxBalance;
  }

  @Column(name = "balance")
  @Getter
  private BigDecimal balance;

  @Transient
  private final BigDecimal maxBalance;

  /**
   * credit balance of the provided amount if it doesn't exceed balance ceiling.
   * Amount can't be negative.
   *
   * @param amount to credit
   * @throws ExceedingBalanceCeilingException if balance ceiling exceeded
   */
  public void credit(BigDecimal amount) throws ExceedingBalanceCeilingException {
    if (amount.signum() == -1) {
      LOGGER.error("The amount to credit can't be negative");
      throw new IllegalArgumentException("The amount to credit can't be negative");
    }
    if (balance.add(amount).compareTo(maxBalance) > 0) {
      LOGGER.error("Exceeding Balance ceiling prevent crediting the amount");
      throw new ExceedingBalanceCeilingException(
          "Exceeding Balance ceiling prevent crediting the amount");
    }
    balance = balance.add(amount);
  }

  /**
   * Debit balance of the provided amount if sufficient provision.
   * Amount can't be negative.
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
