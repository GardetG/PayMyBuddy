package com.openclassrooms.paymybuddy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.config.PageableConfiguration;
import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.BankAccountService;
import com.openclassrooms.paymybuddy.service.CredentialsService;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = BankAccountController.class)
@Import(PageableConfiguration.class)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private CredentialsService credentialsService;

    @Captor
    ArgumentCaptor<BankAccountDto> DtoCaptor;

    private BankAccountDto bankAccountDtoTest;
    private User userTest;
    private User adminTest;
    private JSONObject jsonParam;

    @BeforeEach
    void setUp() {
        bankAccountDtoTest = new BankAccountDto(1, "Primary Account","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456","XXXXXXXXxyz");
        userTest = new User("test","test","user1@mail.com","password", Role.USER, LocalDateTime.now());
        userTest.setUserId(1);
        adminTest = new User("test","test","test@mail.com","password", Role.ADMIN, LocalDateTime.now());
        jsonParam = new JSONObject();
    }

    @DisplayName("GET all user bank accounts should return 200 with page of bank account DTO")
    @Test
    void getAllFromUserTest() throws Exception {
        // GIVEN
        when(bankAccountService.getAllFromUser(anyInt(),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(bankAccountDtoTest)));

        // WHEN
        mockMvc.perform(get("/users/1/bankaccounts").with(user(userTest)))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[0].bankAccountId", is(1)))
            .andExpect(jsonPath("$.content.[0].title", is("Primary Account")))
            .andExpect(jsonPath("$.content.[0].iban", is("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456")))
            .andExpect(jsonPath("$.content.[0].bic", is("XXXXXXXXxyz")));
        verify(bankAccountService, times(1)).getAllFromUser(1, Pageable.unpaged());
    }

    @DisplayName("GET all bank accounts from another user should return 403")
    @Test
    void getAllFromUserWhenIdNotMatchingTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(get("/users/2/bankaccounts").with(user(userTest)))

            // THEN
            .andExpect(status().isForbidden());
        verify(bankAccountService, times(0)).getAllFromUser(anyInt(), any(Pageable.class) );
    }

    @DisplayName("GET all user bank accounts from non existent user should return 404")
    @Test
    void getAllFromUserWhenNotFoundTest() throws Exception {
        // GIVEN
        when(bankAccountService.getAllFromUser(anyInt(), any(Pageable.class) )).thenThrow(
            new ResourceNotFoundException("This user is not found"));

        // WHEN
        mockMvc.perform(get("/users/2/bankaccounts").with(user(adminTest)))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("This user is not found")));
        verify(bankAccountService, times(1)).getAllFromUser(2, Pageable.unpaged());
    }

    @DisplayName("POST bank account to user should return 201 with created bank account DTO")
    @Test
    void addToUserTest() throws Exception {
        // GIVEN
        jsonParam.put("title","Primary Account")
                .put("iban","1234567890abcdefghijklmnopqrstu456")
                .put("bic","12345678xyz");
        when(bankAccountService.addToUser(anyInt(), any(BankAccountDto.class))).thenReturn(bankAccountDtoTest);

        // WHEN
        mockMvc.perform(post("/users/1/bankaccounts").with(user(userTest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParam.toString()))

            // THEN
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.bankAccountId", is(1)))
            .andExpect(jsonPath("$.title", is("Primary Account")))
            .andExpect(jsonPath("$.iban", is("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456")))
            .andExpect(jsonPath("$.bic", is("XXXXXXXXxyz")));
        verify(bankAccountService, times(1)).addToUser(anyInt(), DtoCaptor.capture());
        BankAccountDto excpectedDto =  new BankAccountDto(0, "Primary Account","1234567890abcdefghijklmnopqrstu456","12345678xyz");
        assertThat(DtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(excpectedDto);
    }

    @DisplayName("POST invalid bank account to user should return 422")
    @Test
    void addToUserWithInvalidBankAccountTest() throws Exception {
        // GIVEN
        jsonParam.put("title","   ")
            .put("iban","XX")
            .put("bic","XXXXXXXXXXXXXXX");

        // WHEN
        mockMvc.perform(post("/users/1/bankaccounts").with(user(userTest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParam.toString()))

            // THEN
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.title", is("Title is mandatory")))
            .andExpect(jsonPath("$.iban", is("IBAN should have between 14 and 34 characters")))
            .andExpect(jsonPath("$.bic", is("BIC should have between 8 and 11 characters")));
        verify(bankAccountService, times(0)).addToUser(anyInt(),any(BankAccountDto.class));
    }

    @DisplayName("POST bank account already added to user should return 409")
    @Test
    void addToUserWhenAlreadyExistsTest() throws Exception {
        // GIVEN
        jsonParam.put("title","Primary Account")
            .put("iban","1234567890abcdefghijklmnopqrstu456")
            .put("bic","12345678xyz");
        when(bankAccountService.addToUser(anyInt(), any(BankAccountDto.class))).thenThrow(
            new ResourceAlreadyExistsException("This bank account already exists"));

        // WHEN
        mockMvc.perform(post("/users/1/bankaccounts").with(user(userTest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonParam.toString()))

            // THEN
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$", is("This bank account already exists")));
        verify(bankAccountService, times(1)).addToUser(anyInt(), any(BankAccountDto.class));
    }

    @DisplayName("POST bank account to an other user should return 403")
    @Test
    void addToUserWhenAuthenticateButIdNotMatchingTest() throws Exception {
        // GIVEN
        jsonParam.put("title","Primary Account")
            .put("iban","1234567890abcdefghijklmnopqrstu456")
            .put("bic","12345678xyz");

        // WHEN
        mockMvc.perform(get("/users/2/bankaccounts").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

            // THEN
            .andExpect(status().isForbidden());
        verify(bankAccountService, times(0)).getAllFromUser(anyInt(), any(Pageable.class));
    }

    @DisplayName("DELETE bank account from a user should return 204")
    @Test
    void removeFromUserIdTest() throws Exception {
        // WHEN
        mockMvc.perform(delete("/users/1/bankaccounts/9").with(user(userTest)))

            // THEN
            .andExpect(status().isNoContent());
        verify(bankAccountService, times(1)).removeFromUser(1,9);
    }

    @DisplayName("DELETE bank account when user or account non exists should return 404")
    @Test
    void removeFromUserWhenAccountNotFoundTest() throws Exception {
        // GIVEN
        doThrow(new ResourceNotFoundException("This resource is not found")).when(bankAccountService)
            .removeFromUser(anyInt(),anyInt());

        // WHEN
        mockMvc.perform(delete("/users/9/bankaccounts/9").with(user(adminTest)))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("This resource is not found")));
        verify(bankAccountService, times(1)).removeFromUser(9,9);
    }

    @DisplayName("DELETE bank account from an other user should return 403")
    @Test
    void removeFromUserWhenIdNotMatchingTest() throws Exception {
        // WHEN
        mockMvc.perform(delete("/users/2/bankaccounts/9").with(user(userTest)))

            // THEN
            .andExpect(status().isForbidden());
        verify(bankAccountService, times(0)).removeFromUser(anyInt(),anyInt());
    }

}
