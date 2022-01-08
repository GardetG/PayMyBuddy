package com.openclassrooms.paymybuddy.utils;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.model.Transaction;

/**
 * Mapper utility class for bank transfer.
 */
public class TransactionMapper {

  private TransactionMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a Transaction into a TransactionDto.
   *
   * @param transaction to map
   * @return TransactionDto
   */
  public static TransactionDto toDto(Transaction transaction) {
    TransactionDto transactionDto = new TransactionDto();
    transactionDto.setDescription(transaction.getDescription());
    transactionDto.setAmount(transaction.getAmount());
    transactionDto.setDate(transaction.getDate());
    transactionDto.setEmitterId(transaction.getEmitter().getUserId());
    transactionDto.setEmitterFirstname(transaction.getEmitter().getFirstname());
    transactionDto.setEmitterLastname(transaction.getEmitter().getLastname());
    transactionDto.setReceiverId(transaction.getReceiver().getUserId());
    transactionDto.setReceiverFirstname(transaction.getReceiver().getFirstname());
    transactionDto.setReceiverLastname(transaction.getReceiver().getLastname());
    return transactionDto;
  }
}
