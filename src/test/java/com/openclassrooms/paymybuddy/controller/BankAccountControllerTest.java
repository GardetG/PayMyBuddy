package com.openclassrooms.paymybuddy.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.BankAccountService;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import com.openclassrooms.paymybuddy.utils.JsonParser;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = BankAccountController.class)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private CredentialsService credentialsService;

    private BankAccountDto bankAccountDtoTest;
    private BankAccountDto UnmaskedBankAccountDtoTest;
    private User userTest;
    private User adminTest;

    @BeforeEach
    void setUp() {
        UnmaskedBankAccountDtoTest = new BankAccountDto(1, "Primary Account","1234567890abcdefghijklmnopqrstu456","12345678xyz");
        bankAccountDtoTest = new BankAccountDto(1, "Primary Account","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456","XXXXXXXXxyz");
        userTest = new User("test","test","user1@mail.com","password", User.Role.USER);
        userTest.setUserId(1);
        adminTest = new User("test","test","test@mail.com","password", User.Role.ADMIN);
    }

    @Test
    void getAllByIdTest() throws Exception {
        // GIVEN
        when(bankAccountService.getAllByUserId(anyInt())).thenReturn(List.of(bankAccountDtoTest));

        // WHEN
        mockMvc.perform(get("/users/1/bankaccounts").with(user(userTest)))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].bankAccountId", is(1)))
            .andExpect(jsonPath("$[0].title", is("Primary Account")))
            .andExpect(jsonPath("$[0].iban", is("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456")))
            .andExpect(jsonPath("$[0].bic", is("XXXXXXXXxyz")));
        verify(bankAccountService, times(1)).getAllByUserId(1);
    }

    @Test
    void getAllByIdWhenNotFoundTest() throws Exception {
        // GIVEN
        when(bankAccountService.getAllByUserId(anyInt())).thenThrow(
            new ResourceNotFoundException("This user is not found"));

        // WHEN
        mockMvc.perform(get("/users/2/bankaccounts").with(user(adminTest)))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("This user is not found")));
        verify(bankAccountService, times(1)).getAllByUserId(2);
    }

    @Test
    void getAllByIdNotAuthenticateTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(get("/users/1/bankaccounts"))

            // THEN
            .andExpect(status().isUnauthorized());
        verify(bankAccountService, times(0)).getAllByUserId(anyInt());
    }

    @Test
    void getAllByIdWhenAuthenticateButIdNotMatchingTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(get("/users/2/bankaccounts").with(user(userTest)))

            // THEN
            .andExpect(status().isForbidden());
        verify(bankAccountService, times(0)).getAllByUserId(anyInt());
    }

    @Test
    void addToUserTest() throws Exception {
        // GIVEN
        when(bankAccountService.addToUserId(anyInt(), any(BankAccountDto.class))).thenReturn(List.of(bankAccountDtoTest));

        // WHEN
        mockMvc.perform(post("/users/1/bankaccounts").with(user(userTest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.asString(UnmaskedBankAccountDtoTest)))

            // THEN
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$[0].bankAccountId", is(1)))
            .andExpect(jsonPath("$[0].title", is("Primary Account")))
            .andExpect(jsonPath("$[0].iban", is("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456")))
            .andExpect(jsonPath("$[0].bic", is("XXXXXXXXxyz")));
        verify(bankAccountService, times(1)).addToUserId(anyInt(),any(BankAccountDto.class));
    }

    @Test
    void addToUserWithInvalidBankAccountTest() throws Exception {
        // GIVEN
        BankAccountDto invalidDto = new BankAccountDto(0,"  ","XX","XXXX");

        // WHEN
        mockMvc.perform(post("/users/1/bankaccounts").with(user(userTest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.asString(invalidDto)))

            // THEN
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.title", is("Title is mandatory")))
            .andExpect(jsonPath("$.iban", is("IBAN should have between 14 and 34 characters")))
            .andExpect(jsonPath("$.bic", is("BIC should have between 8 and 11 characters")));
        verify(bankAccountService, times(0)).addToUserId(anyInt(),any(BankAccountDto.class));
    }

    @Test
    void addToUserWhenUserNotFoundTest() throws Exception {
        // GIVEN
        when(bankAccountService.addToUserId(anyInt(), any(BankAccountDto.class))).thenThrow(
            new ResourceNotFoundException("This user is not found"));

        // WHEN
        mockMvc.perform(post("/users/2/bankaccounts").with(user(adminTest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonParser.asString(UnmaskedBankAccountDtoTest)))

            // THEN
            .andExpect(status().isNotFound());
        verify(bankAccountService, times(1)).addToUserId(anyInt(), any(BankAccountDto.class));
    }

    @Test
    void addToUserWhenNotAuthenticateTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(get("/users/1/bankaccounts")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(UnmaskedBankAccountDtoTest)))

            // THEN
            .andExpect(status().isUnauthorized());
        verify(bankAccountService, times(0)).getAllByUserId(anyInt());
    }

    @Test
    void addToUserWhenAuthenticateButIdNotMatchingTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(get("/users/2/bankaccounts").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(UnmaskedBankAccountDtoTest)))

            // THEN
            .andExpect(status().isForbidden());
        verify(bankAccountService, times(0)).getAllByUserId(anyInt());
    }

    @Test
    void deleteByIdTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(delete("/users/1/bankaccounts/9").with(user(userTest)))

            // THEN
            .andExpect(status().isNoContent());
        verify(bankAccountService, times(1)).deleteById(1,9);
    }

    @Test
    void deleteByIdWhenAccountNotFoundTest() throws Exception {
        // GIVEN
        when(bankAccountService.deleteById(anyInt(),anyInt())).thenThrow(
            new ResourceNotFoundException("This account is not found"));

        // WHEN
        mockMvc.perform(delete("/users/1/bankaccounts/99").with(user(userTest)))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("This account is not found")));
        verify(bankAccountService, times(1)).deleteById(1,99);
    }

    @Test
    void deleteByIdWhenUserNotFoundTest() throws Exception {
        // GIVEN
        when(bankAccountService.deleteById(anyInt(),anyInt())).thenThrow(
            new ResourceNotFoundException("This user is not found"));

        // WHEN
        mockMvc.perform(delete("/users/2/bankaccounts/9").with(user(adminTest)))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("This user is not found")));
        verify(bankAccountService, times(1)).deleteById(2,9);
    }

    @Test
    void deleteByIdWhenNotAuthenticateTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(delete("/users/1/bankaccounts/9"))

            // THEN
            .andExpect(status().isUnauthorized());
        verify(bankAccountService, times(0)).deleteById(anyInt(),anyInt());
    }

    @Test
    void deleteByIdWhenAuthenticateButIdNotMatchingTest() throws Exception {
        // GIVEN

        // WHEN
        mockMvc.perform(delete("/users/2/bankaccounts/9").with(user(userTest)))

            // THEN
            .andExpect(status().isForbidden());
        verify(bankAccountService, times(0)).deleteById(anyInt(),anyInt());
    }
}
