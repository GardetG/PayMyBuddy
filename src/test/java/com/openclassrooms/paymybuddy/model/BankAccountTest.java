package com.openclassrooms.paymybuddy.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import com.openclassrooms.paymybuddy.exception.ExceedingBalanceCeilingException;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankAccountTest {

  private BankAccount bankAccount;

  @BeforeEach
  void setUp() {
    bankAccount = new BankAccount("PrimaryAccount", "1234567890abcdefghijklmnopqrstu123","12345678abc");
  }

  @DisplayName("Credit a user bank account")
  @ParameterizedTest(name = "{index} Credit {0} euros")
  @ValueSource(doubles = {0, 0.5, 10, 500})
  void creditBankAccountTest(double input) throws Exception {
    // GIVEN
    BigDecimal amount = BigDecimal.valueOf(input);

    // WHEN
    bankAccount.credit(amount);

    // THEN
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE.add(amount));
  }

  @DisplayName("Credit a user bank account with negative amount should throw an exception")
  @Test
  void creditBankAccountWithNegativeAmountTest() {
    // GIVEN
    BigDecimal amount = BigDecimal.valueOf(-100);

    // WHEN
    assertThatThrownBy(() -> bankAccount.credit(amount))

        // THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount to credit can't be negative");
  }

  @DisplayName("Credit a user bank account above balance ceiling should throw an exception")
  @Test
  void creditBankAccountToExceedBalanceCeilingTest() {
    // GIVEN
    BigDecimal amount = ApplicationValue.BANKACCOUNT_BALANCE_CEILING;

    // WHEN
    assertThatThrownBy(() -> bankAccount.credit(amount))

        // THEN
        .isInstanceOf(ExceedingBalanceCeilingException.class)
        .hasMessageContaining("Exceeding Balance ceiling prevent crediting the amount");
  }

  @DisplayName("Debit a user bank account")
  @ParameterizedTest(name = "{index} Debit {0} euros")
  @ValueSource(doubles = {0, 0.5, 10, 500}) // six numbers
  void debitBankAccountTest(double input) throws Exception {
    // GIVEN
    BigDecimal amount = BigDecimal.valueOf(input);

    // WHEN
    bankAccount.debit(amount);

    // THEN
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE.subtract(amount));
  }

  @DisplayName("Debit a user bank account with negative amount should throw an exception")
  @Test
  void debitBankAccountWithNegativeAmountTest() {
    // GIVEN
    BigDecimal amount = BigDecimal.valueOf(-100);

    // WHEN
    assertThatThrownBy(() -> bankAccount.debit(amount))

        // THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount to debit can't be negative");
  }

  @DisplayName("Debit a user bank account below zero should throw an exception")
  @Test
  void debitBankAccountWithMoreThanProvisionTest() {
    // GIVEN
    BigDecimal amount = bankAccount.getBalance().add(BigDecimal.ONE);

    // WHEN
    assertThatThrownBy(() -> bankAccount.debit(amount))

        // THEN
        .isInstanceOf(InsufficientProvisionException.class)
        .hasMessageContaining("Insufficient provision to debit the amount");
  }
}
