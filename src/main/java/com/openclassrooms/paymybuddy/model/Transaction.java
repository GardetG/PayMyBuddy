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

/**
 * Model Class of a transaction with id, emitter and receiver , amount, description and date.
 */
@Entity
@Table(name = "transaction")
public class Transaction {

  private Transaction() {
    // Private default constructor for hibernate
  }

  /**
   * Parametric constructor with required value to instantiate a valid Transaction.
   *
   * @param emitter     of the transaction
   * @param receiver    of the transaction
   * @param date        of the transaction
   * @param amount      transferred
   * @param description of the transaction
   */
  public Transaction(User emitter, User receiver, LocalDateTime date, BigDecimal amount,
                     String description) {
    this.emitter = emitter;
    this.receiver = receiver;
    this.date = date;
    this.amount = amount;
    this.description = description;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_id")
  @Getter
  @Setter
  private int transactionId;

  @ManyToOne(
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      fetch = FetchType.EAGER)
  @JoinColumn(name = "emitter_Id")
  @Getter
  private User emitter;

  @ManyToOne(
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      fetch = FetchType.EAGER)
  @JoinColumn(name = "receiver_id")
  @Getter
  private User receiver;

  @Column(name = "description")
  @Getter
  private String description;

  @Column(name = "amount")
  @Getter
  private BigDecimal amount;

  @Column(name = "date")
  @Getter
  private LocalDateTime date;

}
