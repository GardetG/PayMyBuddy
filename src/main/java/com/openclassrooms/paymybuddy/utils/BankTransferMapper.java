package com.openclassrooms.paymybuddy.utils;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.model.BankTransfer;
import com.openclassrooms.paymybuddy.model.User;

/**
 * Mapper utility class for bank transfer.
 */
public class BankTransferMapper {

  private BankTransferMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a BankTransfer into a BankTransferDto.
   *
   * @param bankTransfer to map
   * @return BankAccountDto
   */
  public static BankTransferDto toDto(BankTransfer bankTransfer) {
    BankTransferDto bankTransferDto = new BankTransferDto();
    bankTransferDto.setAmount(bankTransfer.getAmount());
    bankTransferDto.setDate(bankTransfer.getDate());
    bankTransferDto.setIncome(bankTransfer.getIsIncome());
    bankTransferDto.setBankAccountId(bankTransfer.getBankAccount().getBankAccountId());
    bankTransferDto.setTitle(bankTransfer.getBankAccount().getTitle());
    User user = bankTransfer.getBankAccount().getUser();
    bankTransferDto.setUserId(user.getUserId());
    bankTransferDto.setFirstname(user.getFirstname());
    bankTransferDto.setLastname(user.getLastname());

    return bankTransferDto;
  }
}
