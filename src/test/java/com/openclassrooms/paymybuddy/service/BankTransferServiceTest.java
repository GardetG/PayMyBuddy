package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
  private BankTransferServiceImpl bankTransferService;

  @MockBean
  private BankTransferRepository bankTransferRepository;

  @MockBean
  private UserService userService;

  private User user;
  private BankAccount bankAccount;
  LocalDateTime date;
  private BigDecimal amount;
  private BankTransfer bankTransferTest;
  private BankTransferDto bankTransferDtoTest;

  @BeforeEach
  void setUp() throws Exception {
    user = new User("user","test","user@mail.com","password",Role.USER, LocalDateTime.now());
    user.setUserId(1);
    bankAccount = new BankAccount("Primary Account", "1234567890abcdefghijklmnopqrstu123","12345678abc");
    bankAccount.setBankAccountId(1);
    user.addBankAccount(bankAccount);
    date = LocalDateTime.now();
    amount = BigDecimal.TEN;
    bankTransferTest = new BankTransfer(bankAccount, date, amount, false);
    bankTransferTest.setBankTransferId(1);
    bankTransferDtoTest = new BankTransferDto(1,1, amount,false,date,"user","test", "Primary Account");
  }

  @DisplayName("Subscribe to user deletion should call user service subscribe method")
  @Test
  void userDeletionSubscribePostConstructTest() {
    // WHEN
    bankTransferService.userDeletionSubscribe();
    // THEN
    verify(userService).userDeletionSubscribe(any(BankTransferServiceImpl.class));
  }

  @DisplayName("Get all should return a page of bank transfer Dto")
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

  @DisplayName("Get all when no bank transfer exists should return an empty")
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

  @DisplayName("Get all from user return a page of transactions Dto")
  @Test
  void getAllFromUserTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenReturn(user);
    when(bankTransferRepository.findByBankAccountIn(anySet(),any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(bankTransferTest)));

    // WHEN
    Page<BankTransferDto> actualPageBankTransferDto = bankTransferService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageBankTransferDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(bankTransferDtoTest));
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(1)).findByBankAccountIn(user.getBankAccounts(),pageable);
  }

  @DisplayName("Get all from user when no bank transfers exist should return an empty page")
  @Test
  void getAllFromUserWhenEmptyTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenReturn(user);
    when(bankTransferRepository.findByBankAccountIn(anySet(), any(Pageable.class)))
        .thenReturn(Page.empty());

    // WHEN
    Page<BankTransferDto> actualPageBankTransferDto = bankTransferService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageBankTransferDto.getContent()).isEmpty();
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(1)).findByBankAccountIn(user.getBankAccounts(),pageable);
  }

  @DisplayName("Get all from non existent user should throw an exception")
  @Test
  void getAllFromUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.getFromUser(9,pageable))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
    verify(bankTransferRepository, times(0)).existsById(anyInt());
  }

  @DisplayName("Requesting debit wallet to bank account should persist a new exiting bank transfer")
  @Test
  void requestExitingTransferTest() throws Exception {
    // GIVEN
    user.credit(amount);
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,false,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(user);
    when(bankTransferRepository.save(any(BankTransfer.class))).thenReturn(bankTransferTest);

    // WHEN
    BankTransferDto actualDto = bankTransferService.requestTransfer(request);

        // THEN
    assertThat(actualDto).usingRecursiveComparison().ignoringFields("date").isEqualTo(bankTransferDtoTest);
    assertThat(user.getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE.add(amount));
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(1)).save(any(BankTransfer.class));
  }

  @DisplayName("Requesting debit wallet when provision insufficient should throw exceptionr")
  @Test
  void requestExitingTransferWithInsufficientProvisionTest() throws Exception {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,false,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(user);

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(InsufficientProvisionException.class)
        .hasMessageContaining("Insufficient provision to debit the amount");
    assertThat(user.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE);
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @DisplayName("Requesting credit wallet from bank account should persist a new incoming bank transfer")
  @Test
  void requestIncomingTransferTest() throws Exception {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,true,null,null,null,null);
    bankTransferTest = new BankTransfer(bankAccount, date, amount, true);
    bankTransferTest.setBankTransferId(1);
    bankTransferDtoTest = new BankTransferDto(1,1, amount,true,date,"user","test", "Primary Account");
    when(userService.retrieveEntity(anyInt())).thenReturn(user);
    when(bankTransferRepository.save(any(BankTransfer.class))).thenReturn(bankTransferTest);

    // WHEN
    BankTransferDto actualDto = bankTransferService.requestTransfer(request);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().ignoringFields("date").isEqualTo(bankTransferDtoTest);
    assertThat(user.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE.add(amount));
    assertThat(bankAccount.getBalance()).isEqualTo(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE.subtract(amount));
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(1)).save(any(BankTransfer.class));
  }

  @DisplayName("Requesting credit wallet from bank account should throw an exception")
  @Test
  void requestIncomngTransferWithInsufficientProvisionTest() throws Exception {
    // GIVEN
    bankAccount.debit(ApplicationValue.INITIAL_BANKACCOUNT_BALANCE);
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.TEN,false,null,null,null,null);
    bankTransferTest = new BankTransfer(bankAccount, date, amount, true);
    bankTransferTest.setBankTransferId(1);
    bankTransferDtoTest = new BankTransferDto(1,1, amount,true,date,"user","test", "Primary Account");
    when(userService.retrieveEntity(anyInt())).thenReturn(user);

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(InsufficientProvisionException.class)
        .hasMessageContaining("Insufficient provision to debit the amount");
    assertThat(user.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    assertThat(bankAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @DisplayName("Requesting transfer when user not exists should throw an exception")
  @Test
  void requestTransferWhenUserNotFoundTest() throws Exception {
    // GIVEN
    BankTransferDto request = new BankTransferDto(9,1,BigDecimal.TEN,false,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @DisplayName("Requesting transfer when account not exists should throw an exception")
  @Test
  void requestTransferWhenAccountNotFoundTest() throws Exception {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,9,BigDecimal.TEN,false,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(user);

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This bank account is not found");
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @DisplayName("Requesting transfer with negative amount should throw an exception")
  @Test
  void requestTransferWithNegativeAmountTest() throws Exception {
    // GIVEN
    BankTransferDto request = new BankTransferDto(1,1,BigDecimal.valueOf(-25),false,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(user);

    // WHEN
    assertThatThrownBy(() ->  bankTransferService.requestTransfer(request))

        // THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount to debit can't be negative");
    verify(userService, times(1)).retrieveEntity(1);
    verify(bankTransferRepository, times(0)).save(any(BankTransfer.class));
  }

  @DisplayName("Clear transfer for an account should delete all transfer associated")
  @Test
  void clearTransfersForAccountTest() {
    // GIVEN
    when(bankTransferRepository.findByBankAccount(any(BankAccount.class)))
        .thenReturn(List.of(bankTransferTest));

    // WHEN
    bankTransferService.clearTransfersForAccount(bankAccount);

    // THEN
    verify(bankTransferRepository,times(1)).delete(bankTransferTest);
  }

  @DisplayName("Clear transfer for an account when no transfer exists should do nothing")
  @Test
  void clearTransfersForAccountWhenEmptyTest() {
    // GIVEN
    when(bankTransferRepository.findByBankAccountIn(anyList(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    // WHEN
    bankTransferService.clearTransfersForAccount(bankAccount);

    // THEN
    verify(bankTransferRepository,times(0)).delete(any(BankTransfer.class));
  }
}
