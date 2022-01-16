package com.openclassrooms.paymybuddy.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserTest {

  private User userTest;

  @BeforeEach
  void setUp() {
    userTest = new User("test","test","test@mail.com","12345678", Role.USER, LocalDateTime.now());
  }

  @Test
  void addBankAccountTest() throws Exception {
    // GIVEN
    BankAccount accountToAdd = new BankAccount("Account","1234567890abcdefghijklmnopqrstu123","12345678abc");

    // WHEN
    userTest.addBankAccount(accountToAdd);

    // THEN
    assertThat(userTest.getBankAccounts()).usingRecursiveComparison().isEqualTo(Set.of(accountToAdd));
    assertThat(accountToAdd.getUser()).isEqualTo(userTest);
  }

  @Test
  void addAlreadyAddedBankAccountTest() throws Exception {
    // GIVEN
    BankAccount accountToAdd = new BankAccount("Account","1234567890abcdefghijklmnopqrstu123","12345678abc");
    userTest.addBankAccount(accountToAdd);

    // WHEN
    assertThatThrownBy(() -> userTest.addBankAccount(accountToAdd))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This bank account already exists");
  }

  @Test
  void removeBankAccountTest() throws Exception {
    // GIVEN
    BankAccount accountToRemove = new BankAccount("Account","1234567890abcdefghijklmnopqrstu123","12345678abc");
    userTest.addBankAccount(accountToRemove);

    // WHEN
    userTest.removeBankAccount(accountToRemove);

    // THEN
    assertThat(userTest.getBankAccounts()).isEmpty();
    assertThat(accountToRemove.getUser()).isNull();
  }

  @Test
  void removeNotAddedBankAccountTest() {
    // GIVEN
    BankAccount accountToRemove = new BankAccount("Account","1234567890abcdefghijklmnopqrstu123","12345678abc");

    // WHEN
    assertThatThrownBy(() -> userTest.removeBankAccount(accountToRemove))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This bank account is not found");
  }

  @Test
  void getBankAccountsReturnImmutableTest() {
    // GIVEN
    BankAccount accountToAdd = new BankAccount("Account","1234567890abcdefghijklmnopqrstu123","12345678abc");
    Set<BankAccount> bankAccounts = userTest.getBankAccounts();

    // WHEN
    assertThatThrownBy(() -> bankAccounts.add(accountToAdd))

        // THEN
        .isInstanceOf(UnsupportedOperationException.class);
  }


  @Test
  void addConnectionTest() throws Exception {
    // GIVEN
    User userToAdd = new User("Test","Test","contact@mail.com", "12345678", Role.USER, LocalDateTime.now());

    // WHEN
    userTest.addConnection(userToAdd);

    // THEN
    assertThat(userTest.getConnections()).contains(userToAdd);
    assertThat(userToAdd.getConnections()).contains(userTest);
  }

  @Test
  void addAlreadyAddedConnectionTest() throws Exception {
    // GIVEN
    User userToAdd = new User("Test","Test","contact@mail.com", "12345678", Role.USER, LocalDateTime.now());
    userTest.addConnection(userToAdd);

    // WHEN
    assertThatThrownBy(() -> userTest.addConnection(userToAdd))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This connection already exists");
  }

  @Test
  void removeConnectionTest() throws Exception {
    // GIVEN
    User userToRemove = new User("Test","Test","contact@mail.com", "12345678", Role.USER, LocalDateTime.now());
    userTest.addConnection(userToRemove);

    // WHEN
    userTest.removeConnection(userToRemove);

    // THEN
    assertThat(userTest.getConnections()).isEmpty();
    assertThat(userToRemove.getConnections()).isEmpty();
  }

  @Test
  void removeNotAddedConnectionTest() {
    // GIVEN
    User userToRemove = new User("Test","Test","contact@mail.com", "12345678", Role.USER, LocalDateTime.now());

    // WHEN
    assertThatThrownBy(() -> userTest.removeConnection(userToRemove))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This connection is not found");
  }

  @Test
  void getConnectionsReturnImmutableTest() {
    // GIVEN
    User userToAdd = new User("Test","Test","contact@mail.com", "12345678", Role.USER, LocalDateTime.now());
    Set<User> connections = userTest.getConnections();

    // WHEN
    assertThatThrownBy(() -> connections.add(userToAdd))

        // THEN
        .isInstanceOf(UnsupportedOperationException.class);
  }
}
