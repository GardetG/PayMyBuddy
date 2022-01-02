package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class BankAccountServiceTest {

  @Autowired
  private BankAccountService bankAccountService;

  @MockBean
  private UserRepository userRepository;

  private User userTest;
  private BankAccountDto account1DtoTest;
  private BankAccountDto account2DtoTest;

  @BeforeEach
  void setUp() {
    userTest = new User(1,"test","test","test@mail.com","12345678", BigDecimal.ZERO, new Role(1,"USER"), new HashSet<>());
    userTest.getBankAccounts().add(new BankAccount(1,"PrimaryAccount", "1234567890abcdefghijklmnopqrstu123","12345678abc",BigDecimal.valueOf(100)));
    userTest.getBankAccounts().add(new BankAccount(1,"PrimaryAccount", "1234567890abcdefghijklmnopqrstu456","12345678def",BigDecimal.valueOf(150)));
    account1DtoTest = new BankAccountDto(1, "PrimaryAccount","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX123","XXXXXXXXabc");
    account2DtoTest = new BankAccountDto(1, "PrimaryAccount","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456","XXXXXXXXdef");
  }

  @Test
  void getAllByUserIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    List<BankAccountDto> actualListBankAccountDto = bankAccountService.getAllByUserId(1);

    // THEN
    assertThat(actualListBankAccountDto).usingRecursiveComparison().isEqualTo(List.of(account1DtoTest,account2DtoTest));
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getAllByUserIdWhenEmptyTest() throws Exception {
    // GIVEN
    userTest.setBankAccounts(new HashSet<>());
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    List<BankAccountDto> actualListBankAccountDto = bankAccountService.getAllByUserId(1);

    // THEN
    assertThat(actualListBankAccountDto).usingRecursiveComparison().isEqualTo(new ArrayList<>());
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getAllByUserIdWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> bankAccountService.getAllByUserId(2))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
  }
}
