package com.openclassrooms.paymybuddy.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.BankAccount;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.BankAccountService;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import com.openclassrooms.paymybuddy.service.UserService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        userTest = new User(1,"test","test","user1@mail.com","password",BigDecimal.ZERO, new Role(0,"USER"),
            Collections.emptySet());
        adminTest = new User(1,"test","test","test@mail.com","password",BigDecimal.ZERO, new Role(0,"ADMIN"),Collections.emptySet());
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
}
