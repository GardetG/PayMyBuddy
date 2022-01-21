package com.openclassrooms.paymybuddy.constant;

import java.math.BigDecimal;

/**
 * Utility Class for defining application constants value.
 */
public class ApplicationValue {

  private ApplicationValue() {
    throw new IllegalStateException("Utility class");
  }

  public static final BigDecimal INITIAL_USER_BALANCE = BigDecimal.ZERO;
  public static final BigDecimal INITIAL_BANKACCOUNT_BALANCE = BigDecimal.valueOf(500);
  public static final BigDecimal USER_BALANCE_CEILING = BigDecimal.valueOf(999999.99);
  public static final BigDecimal BANKACCOUNT_BALANCE_CEILING = BigDecimal.valueOf(999999.99);
  public static final BigDecimal FARE_PERCENTAGE = BigDecimal.valueOf(0.5);

}
