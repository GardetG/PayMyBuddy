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

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.InsufficientProvisionException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankTransfer;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.BankTransferService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = BankTransferController.class)
class BankTransferControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BankTransferService bankTransferService;

  @MockBean
  private CredentialsService credentialsService;

  @Captor
  ArgumentCaptor<BankTransferDto> bankTransferDtoCaptor;

  private User userTest;
  private User adminTest;
  private BankTransferDto bankTransferDtoTest;
  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    userTest = new User("test","test","user1@mail.com","password", Role.USER, LocalDateTime.now());
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN, LocalDateTime.now());
    bankTransferDtoTest = new BankTransferDto(1,1, BigDecimal.TEN,false, LocalDateTime.now(),"user","test", "Primary Account");
    jsonParam = new JSONObject();
  }

  @Test
  void getAllTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(bankTransferService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(bankTransferDtoTest)));

    // WHEN
    mockMvc.perform(get("/banktransfers?page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].userId", is(1)))
        .andExpect(jsonPath("$.content[0].bankAccountId", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(1)));
    verify(bankTransferService, times(1)).getAll(pageable);
  }

  @Test
  void getAllWhenNotAdminTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/banktransfers?page=0&size=10").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(bankTransferService, times(0)).getAll(any(Pageable.class));
  }

  @Test
  void getFromUserTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(bankTransferService.getFromUser(anyInt(),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(bankTransferDtoTest)));

    // WHEN
    mockMvc.perform(get("/banktransfers/user?id=1&page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].userId", is(1)))
        .andExpect(jsonPath("$.content[0].bankAccountId", is(1)))
        .andExpect(jsonPath("$.content[0].amount", is(10)))
        .andExpect(jsonPath("$.content[0].income", is(false)))
        .andExpect(jsonPath("$.content[0].firstname", is("user")))
        .andExpect(jsonPath("$.content[0].lastname", is("test")))
        .andExpect(jsonPath("$.content[0].title", is("Primary Account")));
    verify(bankTransferService, times(1)).getFromUser(1,pageable);
  }

  @Test
  void getFromUserWhenNotFoundTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(bankTransferService.getFromUser(anyInt(),any(Pageable.class))).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/banktransfers/user?id=9&page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(bankTransferService, times(1)).getFromUser(9,pageable);
  }

  @Test
  void getFromUserWhenAuthenticateIdNotMatchingTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/banktransfers/user?id=2").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(bankTransferService, times(0)).getFromUser(anyInt(),any(Pageable.class));
  }

  @Test
  void postRequestTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1).put("bankAccountId",1)
        .put("amount","10").put("isIncome",false);
    when(bankTransferService.requestTransfer(any(BankTransferDto.class))).thenReturn(bankTransferDtoTest);

    // WHEN
    mockMvc.perform(post("/banktransfers").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.bankAccountId", is(1)))
        .andExpect(jsonPath("$.amount", is(10)))
        .andExpect(jsonPath("$.income", is(false)))
        .andExpect(jsonPath("$.firstname", is("user")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.title", is("Primary Account")));
    verify(bankTransferService, times(1)).requestTransfer(bankTransferDtoCaptor.capture());
    BankTransferDto expectedDto = new BankTransferDto(1,1, BigDecimal.TEN,false, null,null,null, null);
    assertThat(bankTransferDtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @Test
  void postRequestWhenAccountNotFoundTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1).put("bankAccountId",9)
        .put("amount","10").put("isIncome",false);
    when(bankTransferService.requestTransfer(any(BankTransferDto.class))).thenThrow(
        new ResourceNotFoundException("This account is not found"));

    // WHEN
    mockMvc.perform(post("/banktransfers").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This account is not found")));
    verify(bankTransferService, times(1)).requestTransfer(bankTransferDtoCaptor.capture());
    BankTransferDto expectedDto = new BankTransferDto(1,9, BigDecimal.TEN,false, null,null,null, null);
    assertThat(bankTransferDtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @Test
  void postRequestWhenProvisionInsufficientTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1).put("bankAccountId",9)
        .put("amount","100").put("isIncome",false);
    when(bankTransferService.requestTransfer(any(BankTransferDto.class))).thenThrow(
        new InsufficientProvisionException("Insufficient provision to debit the amount"));

    // WHEN
    mockMvc.perform(post("/banktransfers").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("Insufficient provision to debit the amount")));
    verify(bankTransferService, times(1)).requestTransfer(bankTransferDtoCaptor.capture());
    BankTransferDto expectedDto = new BankTransferDto(1,9, BigDecimal.valueOf(100),false, null,null,null, null);
    assertThat(bankTransferDtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @Test
  void postInvalidRequestTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1).put("bankAccountId",1)
        .put("amount","-10.00").put("isIncome",false);

    // WHEN
    mockMvc.perform(post("/banktransfers").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.amount", is("Amount can't be negative")));
    verify(bankTransferService, times(0)).requestTransfer(any(BankTransferDto.class));
  }

  @Test
  void postRequestWhenAuthenticateButUserIdNotMatchingTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",2).put("bankAccountId",1)
        .put("amount","10.00").put("isIncome",false);

    // WHEN
    mockMvc.perform(post("/banktransfers").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isForbidden());
    verify(bankTransferService, times(0)).requestTransfer(any(BankTransferDto.class));
  }
}
