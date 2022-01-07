package com.openclassrooms.paymybuddy.model;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Model Class of a bank account with tile, iban, bic and balance.
 */
@Entity
@Table(name = "bank_account")
@Getter
@Setter
public class BankAccount {

  private BankAccount() {
    // Private default constructor for hibernate
  }

  /**
   * BankAccount parametric constructor with required value to instantiate a valid BankAccount.
   *
   * @param title of bank account
   * @param iban of bank account
   * @param bic of bank account
   */
  public BankAccount(String title, String iban, String bic) {
    this.title = title;
    this.iban = iban;
    this.bic = bic;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bank_account_id")
  private int bankAccountId;

  @Column(name = "title",  length = 60)
  private String title;

  @Column(name = "iban", length = 34)
  private String iban;

  @Column(name = "bic",  length = 11)
  private String bic;

  @Column(name = "balance")
  private BigDecimal balance = ApplicationValue.INITIAL_BANKACCOUNT_BALANCE;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BankAccount that = (BankAccount) o;
    return iban.equals(that.iban) && bic.equals(that.bic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iban, bic);
  }
}
