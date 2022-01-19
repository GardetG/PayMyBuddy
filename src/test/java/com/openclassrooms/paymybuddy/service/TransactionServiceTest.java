package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.constant.ApplicationValue;
import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
  private UserService userService;

  private User emitter;
  private User receiver;
  private BigDecimal amount;
  private Transaction transactionTest;
  private TransactionDto transactionDtoTest;

  @BeforeEach
  void setUp() {
    emitter = new User("user1","test","user1@mail.com","password", Role.USER, LocalDateTime.now());
    emitter.setUserId(1);
    receiver = new User("user2","test","user2@mail.com","password", Role.USER, LocalDateTime.now());
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
    when(userService.retrieveEntity(anyInt())).thenReturn(emitter);
    when(transactionRepository.findByEmitterOrReceiver(any(User.class),any(User.class),any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(transactionTest)));

    // WHEN
    Page<TransactionDto> actualPageTransactionDto = transactionService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageTransactionDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(transactionDtoTest));
    verify(userService, times(1)).retrieveEntity(1);
    verify(transactionRepository, times(1)).findByEmitterOrReceiver(emitter,emitter,pageable);
  }

  @Test
  void getAllFromUserWhenEmptyTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenReturn(emitter);
    when(transactionRepository.findByEmitterOrReceiver(any(User.class),any(User.class),any(Pageable.class)))
        .thenReturn(Page.empty());

    // WHEN
    Page<TransactionDto> actualPageTransactionDto = transactionService.getFromUser(1,pageable);

    // THEN
    assertThat(actualPageTransactionDto.getContent()).isEmpty();
    verify(userService, times(1)).retrieveEntity(1);
    verify(transactionRepository, times(1)).findByEmitterOrReceiver(emitter,emitter,pageable);
  }

  @Test
  void getAllFromUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() ->  transactionService.getFromUser(9,pageable))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
    verify(transactionRepository, times(0)).findByEmitterOrReceiver(any(User.class),any(User.class),any(Pageable.class));
  }


  @Test
  void requestTransactionTest() throws Exception {
    // GIVEN
    emitter.credit(BigDecimal.valueOf(10.05));
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(emitter).thenReturn(receiver);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionTest);

    // WHEN
    TransactionDto actualDto = transactionService.requestTransaction(request);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().ignoringFields("date").isEqualTo(transactionDtoTest);
    assertThat(emitter.getBalance()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    assertThat(receiver.getBalance()).isEqualTo(amount);
    verify(userService, times(2)).retrieveEntity(anyInt());
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWithInsufficientProvisionTest() throws Exception {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(emitter).thenReturn(receiver);

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTransaction(request))

        // THEN
        .isInstanceOf(InsufficientProvisionException.class)
        .hasMessageContaining("Insufficient provision to debit the amount");
    assertThat(emitter.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    assertThat(receiver.getBalance()).isEqualTo(ApplicationValue.INITIAL_USER_BALANCE);
    verify(userService, times(2)).retrieveEntity(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWhenEmitterNotFoundTest() throws Exception {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTransaction(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWhenReceiverNotFoundTest() throws Exception {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",amount,null,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(emitter).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTransaction(request))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(2)).retrieveEntity(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @Test
  void requestTransactionWithNegativeAmountTest() throws Exception {
    // GIVEN
    TransactionDto request = new TransactionDto(1,2, "Gift to a friend",BigDecimal.valueOf(-25),null,null,null,null,null);
    when(userService.retrieveEntity(anyInt())).thenReturn(emitter).thenReturn(receiver);

    // WHEN
    assertThatThrownBy(() ->  transactionService.requestTransaction(request))

        // THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount can't be negative");
    verify(userService, times(2)).retrieveEntity(anyInt());
    verify(transactionRepository, times(0)).save(any(Transaction.class));
  }

  @ParameterizedTest
  @CsvSource({"0,0.00","0.1,0.00", "1,0.01","5,0.03","10,0.05"})
  void calculateFareVariousAmountTest(BigDecimal input, BigDecimal expected) {
    // GIVEN

    // WHEN
    BigDecimal actual = transactionService.calculateFare(input);

        // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void calculateFareWithNegativeAmountTest() {
    // GIVEN
    BigDecimal amount = BigDecimal.valueOf(-10);

    // WHEN
    assertThatThrownBy(() ->  transactionService.calculateFare(amount))

        // THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The amount can't be negative");
  }

  @Test
  void clearTransactionForEmitterTest() {
    // GIVEN

    // WHEN
    transactionService.clearTransactionForUser(transactionTest, emitter);

    // THEN
    assertThat(transactionTest.getEmitter()).isNull();
    assertThat(transactionTest.getReceiver()).isEqualTo(receiver);
    verify(transactionRepository, times(0)).delete(any(Transaction.class));
  }

  @Test
  void clearTransactionForReceiverTest() {
    // GIVEN

    // WHEN
    transactionService.clearTransactionForUser(transactionTest, receiver);

    // THEN
    assertThat(transactionTest.getReceiver()).isNull();
    assertThat(transactionTest.getEmitter()).isEqualTo(emitter);
    verify(transactionRepository, times(0)).delete(any(Transaction.class));
  }

  @Test
  void clearTransactionWhenEmitterAndReceiverNullTest() {
    // GIVEN
    transactionTest.setReceiver(null);

    // WHEN
    transactionService.clearTransactionForUser(transactionTest, emitter);

    // THEN
    assertThat(transactionTest.getReceiver()).isNull();
    assertThat(transactionTest.getEmitter()).isNull();
    verify(transactionRepository, times(1)).delete(transactionTest);
  }
}
