package com.openclassrooms.paymybuddy.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model Class of a bank account with tile, iban, bic and owner user oh the account.
 */
@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

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
  private BigDecimal balance;

}
