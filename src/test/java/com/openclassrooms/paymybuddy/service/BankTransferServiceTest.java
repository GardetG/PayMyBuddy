package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.BankTransfer;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.BankTransferRepository;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class BankTransferServiceTest {

  @Autowired
  private BankTransferService bankTransferService;

  @MockBean
  private BankTransferRepository bankTransferRepository;

  @MockBean
  private UserRepository userRepository;

  private User user;
  private BankAccount bankAccount;
  private BigDecimal amount;
  private BankTransfer bankTransferTest;
  private BankTransferDto bankTransferDtoTest;

  @BeforeEach
  void setUp() throws Exception {
    user = new User("user","test","user@mail.com","password",Role.USER);
    user.setUserId(1);
    bankAccount = new BankAccount("Primary Account", "1234567890abcdefghijklmnopqrstu123","12345678abc");
    bankAccount.setBankAccountId(1);
    user.addBankAccount(bankAccount);
    LocalDateTime date = LocalDateTime.now();
    amount = BigDecimal.TEN;
    bankTransferTest = new BankTransfer(bankAccount, date, amount, false);
    bankTransferTest.setBankTransferId(1);
    bankTransferDtoTest = new BankTransferDto(1,1, amount,false,date,"user","test", "Primary Account");
  }

  @Test
  void getAllTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(bankTransferRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(bankTransferTest)));

    // WHEN
    Page<BankTransferDto> actualPagesBankTransferDto = bankTransferService.getAll(pageable);

    // THEN
    assertThat(actualPagesBankTransferDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(bankTransferDtoTest));
    verify(bankTransferRepository, times(1)).findAll(pageable);
  }

  @Test
  void getAllWhenEmptyTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(bankTransferRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    // WHEN
    Page<BankTransferDto> actualPageBankTransferDto = bankTransferService.getAll(pageable);

    // THEN
    assertThat(actualPageBankTransferDto.getContent()).isEmpty();
    verify(bankTransferRepository, times(1)).findAll(pageable);
  }

  @Test
  void getALLFromUserTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.existsById(anyInt())).thenReturn(true);
    when(bankTransferRepository.findByBankAccountUserUserId(anyInt(),any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(bankTransferTest)));

    // WHEN
    Page<BankTransferDto> actualPageBankTransferDto = bankTransferService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageBankTransferDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(bankTransferDtoTest));
    verify(bankTransferRepository, times(1)).findByBankAccountUserUserId(1,pageable);
  }

  @Test
  void getAllFromUserWhenEmptyTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.existsById(anyInt())).thenReturn(true);
    when(bankTransferRepository.findByBankAccountUserUserId(anyInt(),any(Pageable.class)))
        .thenReturn(Page.empty());

    // WHEN
    Page<BankTransferDto> actualPageBankTransferDto = bankTransferService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageBankTransferDto.getContent()).isEmpty();
    verify(userRepository, times(1)).existsById(1);
    verify(bankTransferRepository, times(1)).findByBankAccountUserUserId(1,pageable);
  }

  @Test
  void getAllFromUserWhenUserNotFoundTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.existsById(anyInt())).thenReturn(false);

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.getFromUser(9,pageable))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).existsById(9);
    verify(bankTransferRepository, times(0)).existsById(anyInt());
  }

  @Test
  void requestExitingTransferTest() throws Exception {
    // GIVEN
    user.credit(amount);
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,false,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
    when(bankTransferRepository.save(any(BankTransfer.class))).thenReturn(bankTransferTest);

    // WHEN
    BankTransferDto actualDto = bankTransferService.requestTransfer(request);

        // THEN
    assertThat(actualDto).usingRecursiveComparison().ignoringFields("date").isEqualTo(bankTransferDtoTest);
    assertThat(user.getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE.add(amount));
    verify(userRepository, times(1)).findById(1);
    verify(bankTransferRepository, times(1)).save(any(BankTransfer.class));
  }

  @Test
  void requestExitingTransferWithInsufficientProvisionTest() {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,false,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(InsufficientProvisionException.class)
        .hasMessageContaining("Insufficient provision to debit the amount");
    assertThat(user.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE);
    verify(userRepository, times(1)).findById(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @Test
  void requestIncomingTransferTest() throws Exception {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,true,null,null,null,null);
    bankTransferTest.setIsIncome(true);
    bankTransferDtoTest.setIncome(true);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
    when(bankTransferRepository.save(any(BankTransfer.class))).thenReturn(bankTransferTest);

    // WHEN
    BankTransferDto actualDto = bankTransferService.requestTransfer(request);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().ignoringFields("date").isEqualTo(bankTransferDtoTest);
    assertThat(user.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE.add(amount));
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE.subtract(amount));
    verify(userRepository, times(1)).findById(1);
    verify(bankTransferRepository, times(1)).save(any(BankTransfer.class));
  }

  @Test
  void requestIncomngTransferWithInsufficientProvisionTest() throws Exception {
    // GIVEN
    bankAccount.debit(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE);
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,false,null,null,null,null);
    bankTransferTest.setIsIncome(true);
    bankTransferDtoTest.setIncome(true);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(InsufficientProvisionException.class)
        .hasMessageContaining("Insufficient provision to debit the amount");
    assertThat(user.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    assertThat(bankAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    verify(userRepository, times(1)).findById(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @Test
  void requestTransferWhenUserNotFoundTest() {
    // GIVEN
    BankTransferDto request = new BankTransferDto(9,1,BigDecimal.TEN,false,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(9);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @Test
  void requestTransferWhenAccountNotFoundTest() {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,9,BigDecimal.TEN,false,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This account is not found");
    verify(userRepository, times(1)).findById(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @Test
  void requestTransferWithNegativeAmountTest() {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.valueOf(-25),false,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount to debit can't be negative");
    verify(userRepository, times(1)).findById(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }
}
