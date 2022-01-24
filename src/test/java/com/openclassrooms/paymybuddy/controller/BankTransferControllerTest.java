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

@WebMvcTest(value = BankTransferController.class)
class BankTransferControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BankTransferService bankTransferService;

  @MockBean
  private CredentialsService credentialsService;

  @Captor
  ArgumentCaptor<BankTransferDto> DtoCaptor;

  private User userTest;
  private User adminTest;
  private BankTransferDto bankTransferDtoTest;
  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    LocalDateTime date = LocalDateTime.of(2000,1,1,0,0);
    userTest = new User("test","test","user1@mail.com","password", Role.USER, date);
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN, date);
    bankTransferDtoTest = new BankTransferDto(1,1, BigDecimal.TEN,false, date,"user","test", "Primary Account");
    jsonParam = new JSONObject();
  }

  @DisplayName("GET all bank transfers should return 200 with page of bank transfer Dto")
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

  @DisplayName("GET all bank transfers when not admin should return 403")
  @Test
  void getAllWhenNotAdminTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/banktransfers?page=0&size=10").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(bankTransferService, times(0)).getAll(any(Pageable.class));
  }

  @DisplayName("GET all bank transfer from user should return 200 with page of bank transfer Dto")
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
        .andExpect(jsonPath("$.content[0].isIncome", is(false)))
        .andExpect(jsonPath("$.content[0].date", is("2000-01-01 at 00:00")))
        .andExpect(jsonPath("$.content[0].firstname", is("user")))
        .andExpect(jsonPath("$.content[0].lastname", is("test")))
        .andExpect(jsonPath("$.content[0].title", is("Primary Account")));
    verify(bankTransferService, times(1)).getFromUser(1,pageable);
  }

  @DisplayName("GET all bank transfer from non existent user should return 404")
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

  @DisplayName("GET all bank transfer from an other user should return 403")
  @Test
  void getFromUserWhenAuthenticateIdNotMatchingTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/banktransfers/user?id=2").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(bankTransferService, times(0)).getFromUser(anyInt(),any(Pageable.class));
  }

  @DisplayName("POST request transfer should return 201 with bank transfer DTO")
  @Test
  void postRequestTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1)
        .put("bankAccountId",1)
        .put("amount","10")
        .put("isIncome",false);
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
        .andExpect(jsonPath("$.isIncome", is(false)))
        .andExpect(jsonPath("$.firstname", is("user")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.title", is("Primary Account")));
    verify(bankTransferService, times(1)).requestTransfer(DtoCaptor.capture());
    BankTransferDto expectedDto = new BankTransferDto(1,1, BigDecimal.TEN,false, null,null,null, null);
    assertThat(DtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
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
        .andExpect(jsonPath("$.amount", is("Amount must be greater then 1.00")));
    verify(bankTransferService, times(0)).requestTransfer(any(BankTransferDto.class));
  }

  @DisplayName("POST request transfer on non existent bank account should return 404")
  @Test
  void postRequestWhenAccountNotFoundTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1)
        .put("bankAccountId",9)
        .put("amount","10"
        ).put("isIncome",false);
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
    verify(bankTransferService, times(1)).requestTransfer(DtoCaptor.capture());
    BankTransferDto expectedDto = new BankTransferDto(1,9, BigDecimal.TEN,false, null,null,null, null);
    assertThat(DtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("POST request transfer when insufficient provision should return 409")
  @Test
  void postRequestWhenProvisionInsufficientTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1)
        .put("bankAccountId",9)
        .put("amount","500")
        .put("isIncome",false);
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
    verify(bankTransferService, times(1)).requestTransfer(DtoCaptor.capture());
    BankTransferDto expectedDto = new BankTransferDto(1,9, BigDecimal.valueOf(500),false, null,null,null, null);
    assertThat(DtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("POST request transfer on other user should return 403")
  @Test
  void postRequestWhenAuthenticateButUserIdNotMatchingTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",2)
        .put("bankAccountId",1)
        .put("amount","10.00")
        .put("isIncome",false);

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
