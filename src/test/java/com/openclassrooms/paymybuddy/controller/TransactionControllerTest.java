package com.openclassrooms.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.TransactionService;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = TransactionController.class)
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService transactionService;

  @MockBean
  private CredentialsService credentialsService;

  @Captor
  ArgumentCaptor<TransactionDto> transactionDtoCaptor;

  private User userTest;
  private User adminTest;
  private TransactionDto transactionDtoTest;
  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    LocalDateTime date = LocalDateTime.of(2000,1,1,0,0);
    userTest = new User("test","test","user1@mail.com","password", Role.USER, date);
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN, date);
    transactionDtoTest = new TransactionDto(1,2,"Gift for a friend",BigDecimal.TEN, date,"user1","test","user2","test");
    jsonParam = new JSONObject();
  }

  @DisplayName("GET all transaction should return 200 with page of transaction Dto")
  @Test
  void getAllTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(transactionService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(
        transactionDtoTest)));

    // WHEN
    mockMvc.perform(get("/transactions?page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].emitterId", is(1)))
        .andExpect(jsonPath("$.content[0].receiverId", is(2)))
        .andExpect(jsonPath("$.content[0].description", is("Gift for a friend")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(1)));
    verify(transactionService, times(1)).getAll(pageable);
  }

  @DisplayName("GET all transactions when not admin should return 403")
  @Test
  void getAllWhenNotAdminTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/transactions?page=0&size=10").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(transactionService, times(0)).getAll(any(Pageable.class));
  }

  @DisplayName("GET all transactions from user should return 200 with page of transaction Dto")
  @Test
  void getFromUserTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(transactionService.getFromUser(anyInt(),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(
        transactionDtoTest)));

    // WHEN
    mockMvc.perform(get("/transactions/user?id=1&page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].emitterId", is(1)))
        .andExpect(jsonPath("$.content[0].receiverId", is(2)))
        .andExpect(jsonPath("$.content[0].description", is("Gift for a friend")))
        .andExpect(jsonPath("$.content[0].amount", is(10)))
        .andExpect(jsonPath("$.content[0].date", is("2000-01-01 at 00:00")))
        .andExpect(jsonPath("$.content[0].emitterFirstname", is("user1")))
        .andExpect(jsonPath("$.content[0].emitterLastname", is("test")))
        .andExpect(jsonPath("$.content[0].receiverFirstname", is("user2")))
        .andExpect(jsonPath("$.content[0].receiverLastname", is("test")));
    verify(transactionService, times(1)).getFromUser(1,pageable);
  }

  @DisplayName("GET all transactions from non existent user should return 404")
  @Test
  void getFromUserWhenNotFoundTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(transactionService.getFromUser(anyInt(),any(Pageable.class))).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/transactions/user?id=9&page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(transactionService, times(1)).getFromUser(9,pageable);
  }

  @DisplayName("GET all transactions from an other user should return 403")
  @Test
  void getFromUserIdNotMatchingTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/transactions/user?id=2").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(transactionService, times(0)).getFromUser(anyInt(),any(Pageable.class));
  }

  @DisplayName("POST request transaction should return 201 with transaction DTO")
  @Test
  void postRequestTest() throws Exception {
    // GIVEN
    jsonParam.put("emitterId",1)
        .put("receiverId",2)
        .put("description","Gift for a friend")
        .put("amount",10);
    when(transactionService.requestTransaction(any(TransactionDto.class))).thenReturn(transactionDtoTest);

    // WHEN
    mockMvc.perform(post("/transactions").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.emitterId", is(1)))
        .andExpect(jsonPath("$.receiverId", is(2)))
        .andExpect(jsonPath("$.description", is("Gift for a friend")))
        .andExpect(jsonPath("$.amount", is(10)))
        .andExpect(jsonPath("$.emitterFirstname", is("user1")))
        .andExpect(jsonPath("$.emitterLastname", is("test")))
        .andExpect(jsonPath("$.receiverFirstname", is("user2")))
        .andExpect(jsonPath("$.receiverLastname", is("test")));
    verify(transactionService, times(1)).requestTransaction(transactionDtoCaptor.capture());
    TransactionDto expectedDto = new TransactionDto(1,2,"Gift for a friend",BigDecimal.TEN,null,null,null,null,null);
    assertThat(transactionDtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("POST invalid request transaction should return 422")
  @Test
  void postInvalidRequestTest() throws Exception {
    // GIVEN
    jsonParam.put("emitterId",1)
        .put("receiverId",2)
        .put("description","")
        .put("amount",-10);

    // WHEN
    mockMvc.perform(post("/transactions").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.description", is("Description is mandatory")))
        .andExpect(jsonPath("$.amount", is("Amount must be greater then 1.00")));
    verify(transactionService, times(0)).requestTransaction(any(TransactionDto.class));
  }

  @DisplayName("POST request transaction to non existent user should return 404")
  @Test
  void postRequestWhenNotFoundTest() throws Exception {
    // GIVEN
    jsonParam.put("emitterId",1)
        .put("receiverId",9)
        .put("description","Gift for a friend")
        .put("amount",100);
    when(transactionService.requestTransaction(any(TransactionDto.class))).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(post("/transactions").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(transactionService, times(1)).requestTransaction(transactionDtoCaptor.capture());
    TransactionDto expectedDto = new TransactionDto(1,9,"Gift for a friend",BigDecimal.valueOf(100),null,null,null,null,null);
    assertThat(transactionDtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("POST request transaction with insufficient provision should return 409")
  @Test
  void postRequestWhenProvisionInsufficientTest() throws Exception {
    // GIVEN
    jsonParam.put("emitterId",1)
        .put("receiverId",2)
        .put("description","Gift for a friend")
        .put("amount",100);
    when(transactionService.requestTransaction(any(TransactionDto.class))).thenThrow(
        new InsufficientProvisionException("Insufficient provision to debit the amount"));

    // WHEN
    mockMvc.perform(post("/transactions").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("Insufficient provision to debit the amount")));
    verify(transactionService, times(1)).requestTransaction(transactionDtoCaptor.capture());
    TransactionDto expectedDto = new TransactionDto(1,2,"Gift for a friend",BigDecimal.valueOf(100),null,null,null,null,null);
    assertThat(transactionDtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("POST request transaction from other user should return 403")
  @Test
  void postRequestWhenAuthenticateButUserIdNotMatchingTest() throws Exception {
    // GIVEN
    jsonParam.put("emitterId",2)
        .put("receiverId",1)
        .put("description","Gift for a friend")
        .put("amount",100);

    // WHEN
    mockMvc.perform(post("/transactions").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isForbidden());
    verify(transactionService, times(0)).requestTransaction(any(TransactionDto.class));
  }

}
