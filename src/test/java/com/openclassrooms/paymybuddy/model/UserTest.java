package com.openclassrooms.paymybuddy.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserTest {

  private User userTest;

  @BeforeEach
  void setUp() {
    userTest = new User("test","test","test@mail.com","12345678", Role.USER, LocalDateTime.now());
  }

  @DisplayName("Add a bank account to user")
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

  @DisplayName("Add a bank account already added should throw exception")
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

  @DisplayName("Remove a bank account from user")
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

  @DisplayName("Remove a bank account that has not been added should throw an exception")
  @Test
  void removeBankAccountWhenNotFoundTest() {
    // GIVEN
    BankAccount accountToRemove = new BankAccount("Account","1234567890abcdefghijklmnopqrstu123","12345678abc");

    // WHEN
    assertThatThrownBy(() -> userTest.removeBankAccount(accountToRemove))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This bank account is not found");
  }

  @DisplayName("User bank accounts getter should return an immutable set")
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

  @DisplayName("Add a connection to user")
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

  @DisplayName("Add a connection already added should throw an exception")
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

  @DisplayName("Remove a connection from a user")
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

  @DisplayName("Remove a connection that has not been added should throw an exception")
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

  @DisplayName("User connections getter should return an immutable set")
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

  @DisplayName("Clear user connection should empty connection set")
  @Test
  void clearConnectionTest() throws Exception {
    // GIVEN
    User addedUser = new User("Test","Test","contact@mail.com", "12345678", Role.USER, LocalDateTime.now());
    userTest.addConnection(addedUser);

    // WHEN
    userTest.clearConnection();

    // THEN
    assertThat(userTest.getConnections()).isEmpty();
    assertThat(addedUser.getConnections()).isEmpty();
  }

}

