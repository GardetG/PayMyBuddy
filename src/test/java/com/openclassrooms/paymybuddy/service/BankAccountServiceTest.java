package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class BankAccountServiceTest {

  @Autowired
  private BankAccountService bankAccountService;

  @MockBean
  private UserService userService;

  private User userTest;
  private BankAccountDto accountDtoTest;

  @BeforeEach
  void setUp() throws Exception {
    userTest =
        new User("test", "test", "test@mail.com", "12345678", Role.USER, LocalDateTime.now());
    userTest.setUserId(1);
    BankAccount bankAccountTest =
        new BankAccount("PrimaryAccount", "1234567890abcdefghijklmnopqrstu123", "12345678abc");
    bankAccountTest.setBankAccountId(1);
    userTest.addBankAccount(bankAccountTest);
    accountDtoTest = new BankAccountDto(1, "PrimaryAccount", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX123",
        "XXXXXXXXabc");
  }

  @DisplayName("Get all bank accounts of a user should return a page of DTO")
  @Test
  void getAllFromUserIdTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0, 1);
    when(userService.retrieveEntity(anyInt())).thenReturn(userTest);

    // WHEN
    Page<BankAccountDto> actualPage = bankAccountService.getAllFromUser(1, pageable);

    // THEN
    assertThat(actualPage.getContent()).usingRecursiveComparison()
        .isEqualTo(List.of(accountDtoTest));
    verify(userService, times(1)).retrieveEntity(1);
  }

  @DisplayName("Get all bank accounts of a user when bankaccounts is empty should return an empty page")
  @Test
  void getAllFromUserWhenEmptyTest() throws Exception {
    // GIVEN
    userTest =
        new User("test", "test", "test@mail.com", "12345678", Role.USER, LocalDateTime.now());
    Pageable pageable = PageRequest.of(0, 1);
    when(userService.retrieveEntity(anyInt())).thenReturn(userTest);

    // WHEN
    Page<BankAccountDto> actualPage = bankAccountService.getAllFromUser(1, pageable);

    // THEN
    assertThat(actualPage.getContent()).isEmpty();
    verify(userService, times(1)).retrieveEntity(1);
  }

  @DisplayName("Get all bank accounts of a non-existent user should throw an exception")
  @Test
  void getAllFromByUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0, 1);
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() -> bankAccountService.getAllFromUser(9, pageable))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
  }

  @DisplayName("Add a bank accounts to a user")
  @Test
  void addToUserTest() throws Exception {
    // GIVEN
    BankAccountDto accountToAddDto =
        new BankAccountDto(0, "PrimaryAccount", "1234567890abcdefghijklmnopqrstu456","12345678xyz");
    BankAccountDto maskedAccountDto =
        new BankAccountDto(0, "PrimaryAccount", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456","XXXXXXXXxyz");
    when(userService.retrieveEntity(anyInt())).thenReturn(userTest);
    when(userService.saveEntity(any(User.class))).thenReturn(userTest);

    // WHEN
    BankAccountDto actualBankAccountDto = bankAccountService.addToUser(1, accountToAddDto);

    // THEN
    assertThat(actualBankAccountDto).usingRecursiveComparison().isEqualTo(maskedAccountDto);
    assertThat(userTest.getBankAccounts().size()).isEqualTo(2);
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService, times(1)).saveEntity(any(User.class));
  }

  @DisplayName("Add a bank accounts to a non existent user should throw an exception")
  @Test
  void addToUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    BankAccountDto accountToAddDto =
        new BankAccountDto(0, "SecondaryAccount", "1234567890abcedfghijklmnopqrst789","12345678xyz");
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));


    // WHEN
    assertThatThrownBy(() -> bankAccountService.addToUser(9, accountToAddDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
    verify(userService, times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Add a bank accounts already added should throw an exception")
  @Test
  void addToUserWithAlreadyAddedBankAccountTest() throws Exception {
    // GIVEN
    BankAccountDto accountToAddDto =
        new BankAccountDto(0, "PrimaryAccount", "1234567890abcdefghijklmnopqrstu123","12345678abc");
    when(userService.retrieveEntity(anyInt())).thenReturn(userTest);
    when(userService.saveEntity(any(User.class))).thenReturn(userTest);

    // WHEN
    assertThatThrownBy(() -> bankAccountService.addToUser(2, accountToAddDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This bank account already exists");
    assertThat(userTest.getBankAccounts().size()).isEqualTo(1);
    verify(userService, times(1)).retrieveEntity(2);
    verify(userService, times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Remove a bank accounts from a user")
  @Test
  void removeFromUserTest() throws Exception {
    // GIVEN
    when(userService.retrieveEntity(anyInt())).thenReturn(userTest);
    when(userService.saveEntity(any(User.class))).thenReturn(userTest);

    // WHEN
    bankAccountService.removeFromUser(1, 1);

    // THEN
    assertThat(userTest.getBankAccounts()).isEmpty();
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService, times(1)).saveEntity(any(User.class));
  }

  @DisplayName("Remove a bank accounts to a non existent user should throw an exception")
  @Test
  void removeFromUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() -> bankAccountService.removeFromUser(9, 1))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
    verify(userService, times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Remove a non-existent bank accounts from a user should throw an exception")
  @Test
  void deleteByIdWhenAccountNotFoundTest() throws Exception {
    // GIVEN
    when(userService.retrieveEntity(anyInt())).thenReturn(userTest);

    // WHEN
    assertThatThrownBy(() -> bankAccountService.removeFromUser(1, 9))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This bank account is not found");
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService, times(0)).saveEntity(any(User.class));
  }

}
