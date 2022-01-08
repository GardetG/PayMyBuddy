package com.openclassrooms.paymybuddy.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.TransactionDto;
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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = TransactionController.class)
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService btransactionService;

  @MockBean
  private CredentialsService credentialsService;

  @Captor
  ArgumentCaptor<TransactionDto> btransactionDtoCaptor;

  private User userTest;
  private User adminTest;
  private TransactionDto transactionDtoTest;
  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    userTest = new User("test","test","user1@mail.com","password", Role.USER);
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN);
    transactionDtoTest = new TransactionDto(1,2,"Gift for a friend",BigDecimal.TEN,LocalDateTime.now(),"user1","test","user2","test");
    jsonParam = new JSONObject();
  }

  @Test
  void getAllTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(btransactionService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(
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
    verify(btransactionService, times(1)).getAll(pageable);
  }

  @Test
  void getAllWhenNotAdminTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/transactions?page=0&size=10").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(btransactionService, times(0)).getAll(any(Pageable.class));
  }

  @Test
  void getFromUserTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(btransactionService.getFromUser(anyInt(),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(
        transactionDtoTest)));

    // WHEN
    mockMvc.perform(get("/transactions/user?id=1&page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].emitterId", is(1)))
        .andExpect(jsonPath("$.content[0].receiverId", is(2)))
        .andExpect(jsonPath("$.content[0].description", is("Gift for a friend")))
        .andExpect(jsonPath("$.content[0].amount", is(10)))
        .andExpect(jsonPath("$.content[0].emitterFirstname", is("user1")))
        .andExpect(jsonPath("$.content[0].emitterLastname", is("test")))
        .andExpect(jsonPath("$.content[0].receiverFirstname", is("user2")))
        .andExpect(jsonPath("$.content[0].receiverLastname", is("test")));
    verify(btransactionService, times(1)).getFromUser(1,pageable);
  }

  @Test
  void getFromUserWhenNotFoundTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(btransactionService.getFromUser(anyInt(),any(Pageable.class))).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/transactions/user?id=9&page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(btransactionService, times(1)).getFromUser(9,pageable);
  }

  @Test
  void getFromUserWhenAuthenticateIdNotMatchingTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/transactions/user?id=2").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(btransactionService, times(0)).getFromUser(anyInt(),any(Pageable.class));
  }
}
