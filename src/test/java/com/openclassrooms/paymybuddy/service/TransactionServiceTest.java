package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.BankTransfer;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.BankTransferRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
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
class TransactionServiceTest {

  @Autowired
  private TransactionService transactionService;

  @MockBean
  private TransactionRepository transactionRepository;

  @MockBean
  private UserRepository userRepository;

  private User emitter;
  private User receiver;
  private BigDecimal amount;
  private Transaction transactionTest;
  private TransactionDto transactionDtoTest;

  @BeforeEach
  void setUp() {
    emitter = new User("user1","test","user1@mail.com","password", Role.USER);
    emitter.setUserId(1);
    receiver = new User("user2","test","user2@mail.com","password", Role.USER);
    receiver.setUserId(2);
    LocalDateTime date = LocalDateTime.now();
    amount = BigDecimal.TEN;
    transactionTest = new Transaction(emitter,receiver, date, amount, "Gift to a friend");
    transactionTest.setTransactionId(1);
    transactionDtoTest = new TransactionDto(1,2, "Gift to a friend",amount,date,"user1","test", "user2","test");
  }

  @Test
  void getAllTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(transactionRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(transactionTest)));

    // WHEN
    Page<TransactionDto> actualPageTransactionDto = transactionService.getAll(pageable);

    // THEN
    assertThat(actualPageTransactionDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(transactionDtoTest));
    verify(transactionRepository, times(1)).findAll(pageable);
  }

  @Test
  void getAllWhenEmptyTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(transactionRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    // WHEN
    Page<TransactionDto> actualPageTransactionDto = transactionService.getAll(pageable);

    // THEN
    assertThat(actualPageTransactionDto.getContent()).isEmpty();
    verify(transactionRepository, times(1)).findAll(pageable);
  }

  @Test
  void getAllFromUserTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(emitter));
    when(transactionRepository.findByEmitterOrReceive(any(User.class),any(User.class),any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(transactionTest)));

    // WHEN
    Page<TransactionDto> actualPageTransactionDto = transactionService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageTransactionDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(transactionDtoTest));
    verify(userRepository, times(1)).findById(1);
    verify(transactionRepository, times(1)).findByEmitterOrReceive(emitter,emitter,pageable);
  }

  @Test
  void getAllFromUserWhenEmptyTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(emitter));
    when(transactionRepository.findByEmitterOrReceive(any(User.class),any(User.class),any(Pageable.class)))
        .thenReturn(Page.empty());

    // WHEN
    Page<TransactionDto> actualPageTransactionDto = transactionService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageTransactionDto.getContent()).isEmpty();
    verify(userRepository, times(1)).findById(1);
    verify(transactionRepository, times(1)).findByEmitterOrReceive(emitter,emitter,pageable);
  }

  @Test
  void getAllFromUserWhenUserNotFoundTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() ->  transactionService.getFromUser(9,pageable))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(9);
    verify(transactionRepository, times(0)).findByEmitterOrReceive(any(User.class),any(User.class),any(Pageable.class));
  }


  @Test
  void requestTransactionTest() throws Exception {
    // GIVEN
    emitter.credit(amount);
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(emitter)).thenReturn(Optional.of(receiver));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionTest);

    // WHEN
    TransactionDto actualDto = transactionService.requestTansaction(request);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().ignoringFields("date").isEqualTo(transactionDtoTest);
    assertThat(emitter.getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(receiver.getBalance()).isEqualTo(amount);
    verify(userRepository, times(2)).findById(anyInt());
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWithInsufficientProvisionTest() {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(emitter)).thenReturn(Optional.of(receiver));

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTansaction(request))

        // THEN
        .isInstanceOf(InsufficientProvisionException.class)
        .hasMessageContaining("Insufficient provision to debit the amount");
    assertThat(emitter.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    assertThat(receiver.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    verify(userRepository, times(2)).findById(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWhenEmitterNotFoundTest() {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTansaction(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWhenReceiverNotFoundTest() {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(emitter)).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTansaction(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(2)).findById(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWithNegativeAmountTest() {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",BigDecimal.valueOf(-25),null,null,null,null,null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(emitter)).thenReturn(Optional.of(receiver));

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTansaction(request))

        // THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount to debit can't be negative");
    verify(userRepository, times(2)).findById(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }
}
