package com.openclassrooms.paymybuddy.utils;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.model.BankAccount;

/**
 * Mapper utility class for BankAccount.
 */
public class BankAccountMapper {

  private BankAccountMapper() {
    throw new IllegalStateException("Utility class");
  }

  private static final int UNMASKED_CHARACTERS = 3;
  private static final String MASK_REGEX = String.format(".(?=.{%s})", UNMASKED_CHARACTERS);

  /**
   * Map a BankAccount into a BankAccountDto.
   *
   * @param bankAccount to map
   * @return BankAccountDto
   */
  public static BankAccountDto toDto(BankAccount bankAccount) {
    return new BankAccountDto(
        bankAccount.getBankAccountId(),
        bankAccount.getTitle(),
        bankAccount.getIban().replaceAll(MASK_REGEX, "X"),
        bankAccount.getBic().replaceAll(MASK_REGEX, "X")
    );
  }

  /**
   * Map a BankAccountDto into a new BankAccount.
   *
   * @param bankAccountDto to map
   * @return user to create
   */
  public static BankAccount toModel(BankAccountDto bankAccountDto) {
    return new BankAccount(
        bankAccountDto.getTitle(),
        bankAccountDto.getIban(),
        bankAccountDto.getBic());
  }
}
