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
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class BankAccountServiceTest {

  @Autowired
  private BankAccountService bankAccountService;

  @MockBean
  private UserRepository userRepository;

  private User userTest;
  private BankAccount bankAccountTest;
  private BankAccountDto account1DtoTest;

  @BeforeEach
  void setUp() throws Exception {
    userTest = new User("test","test","test@mail.com","12345678", Role.USER);
    userTest.setUserId(1);
    bankAccountTest = new BankAccount("PrimaryAccount", "1234567890abcdefghijklmnopqrstu123","12345678abc");
    bankAccountTest.setBankAccountId(1);
    userTest.addBankAccount(bankAccountTest);
    account1DtoTest = new BankAccountDto(1, "PrimaryAccount","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX123","XXXXXXXXabc");
  }

  @Test
  void getAllByUserIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    Page<BankAccountDto> actualListBankAccountDto = bankAccountService.getAllFromUser(1, Pageable.unpaged());

    // THEN
    assertThat(actualListBankAccountDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(account1DtoTest));
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getAllByUserIdWhenEmptyTest() throws Exception {
    // GIVEN
    userTest = new User("test","test","test@mail.com","12345678", Role.USER);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    Page<BankAccountDto> actualListBankAccountDto = bankAccountService.getAllFromUser(1, Pageable.unpaged());

    // THEN
    assertThat(actualListBankAccountDto.getContent()).usingRecursiveComparison().isEqualTo(new ArrayList<>());
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getAllByUserIdWhenUserNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> bankAccountService.getAllFromUser(2, Pageable.unpaged()))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
  }

  @Test
  void addToUserIdTest() throws Exception {
    // GIVEN
    BankAccountDto accountToAddDto = new BankAccountDto(0, "PrimaryAccount","1234567890abcdefghijklmnopqrstu456","12345678xyz");
    BankAccountDto maskedAccountDto = new BankAccountDto(0, "PrimaryAccount","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456","XXXXXXXXxyz");
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.save(any(User.class))).thenReturn(userTest);

    // WHEN
    BankAccountDto actualBankAccountDto = bankAccountService.addToUser(1,accountToAddDto);

    // THEN
    assertThat(actualBankAccountDto).usingRecursiveComparison().isEqualTo(maskedAccountDto);
    assertThat(userTest.getBankAccounts().size()).isEqualTo(2);
    verify(userRepository, times(1)).findById(1);
    verify(userRepository,times(1)).save(any(User.class));
  }

  @Test
  void addToUserIdWhenUserNotFoundTest() {
    // GIVEN
    BankAccountDto accountToAddDto = new BankAccountDto(0, "SecondaryAccount","1234567890abcedfghijklmnopqrst789","12345678xyz");
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> bankAccountService.addToUser(2,accountToAddDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void addToUserIdWithAlreadyAddedBankAccountTest() {
    // GIVEN
    BankAccountDto accountToAddDto = new BankAccountDto(0, "PrimaryAccount","1234567890abcdefghijklmnopqrstu123","12345678abc");
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.save(any(User.class))).thenReturn(userTest);

    // WHEN
    assertThatThrownBy(() -> bankAccountService.addToUser(2,accountToAddDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This bank account already exists");
    verify(userRepository, times(1)).findById(2);
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void deleteByIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.save(any(User.class))).thenReturn(userTest);

    // WHEN
    bankAccountService.removeFromUser(1,1);

    // THEN
    verify(userRepository, times(1)).findById(1);
    verify(userRepository,times(1)).save(any(User.class));
  }

  @Test
  void deleteByIdWhenUserNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> bankAccountService.removeFromUser(2,1))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void deleteByIdWhenAccountNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    assertThatThrownBy(() -> bankAccountService.removeFromUser(1,2))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This bank account is not found");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository,times(0)).save(any(User.class));
  }

}
