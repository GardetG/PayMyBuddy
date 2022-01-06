package com.openclassrooms.paymybuddy.constant;

/**
 * Utility Class for defining error messages.
 */
public class ErrorMessage {

  private ErrorMessage() {
    throw new IllegalStateException("Utility class");
  }

  public static final String USER_NOT_FOUND = "This user is not found";
  public static final String BANKACCOUNT_NOT_FOUND = "This bank account is not found";
  public static final String CONNECTION_NOT_FOUND = "This connection is not found";

  public static final String EMAIL_ALREADY_EXIST = "This email is already used";
  public static final String BANKACCOUNT_ALREADY_EXIST = "This bank account already exists";
  public static final String CONNECTION_ALREADY_EXIST = "This connection already exists";

}
