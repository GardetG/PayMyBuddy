package com.openclassrooms.paymybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Model Class of a bank transfer between a user and a bank account.
 */
@Entity
@Table(name = "bank_transfer")
public class BankTransfer {

  private BankTransfer() {
    // Private default constructor for hibernate
  }

  /**
   * Parametric constructor with required value to instantiate a valid BankTransfer.
   *
   * @param bankAccount of the transfer
   * @param date        of the transfer
   * @param amount      transferred
   * @param isIncome    if transferred to user wallet
   */
  public BankTransfer(BankAccount bankAccount, LocalDateTime date, BigDecimal amount,
                      boolean isIncome) {
    this.bankAccount = bankAccount;
    this.date = date;
    this.amount = amount;
    this.isIncome = isIncome;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bank_transfer_id")
  @Getter @Setter
  private int bankTransferId;

  @ManyToOne(
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      fetch = FetchType.EAGER)
  @JoinColumn(name = "bank_account_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Getter
  private BankAccount bankAccount;

  @Column(name = "date")
  @Getter
  private LocalDateTime date;

  @Column(name = "amount")
  @Getter
  private BigDecimal amount;

  @Column(name = "is_income")
  @Getter
  private Boolean isIncome;
}
