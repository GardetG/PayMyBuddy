package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.integration.utils.CredentialUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BankAccountIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    jsonParam = new JSONObject();
  }

  @DisplayName("Add a bank account")
  @Test
  void addBankAccountTest() throws Exception {
    // GIVEN User 1 don't have any bank account registered
    jsonParam.put("title","My Account")
        .put("iban","1234567890abcdefghijklmnopqrstu456")
        .put("bic","12345678xyz");

    // WHEN
    mockMvc.perform(post("/users/3/bankaccounts")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user1@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))
    // THEN bank account successfully added
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.bankAccountId", is(2)));

    // WHEN
    mockMvc.perform(get("/users/3/bankaccounts")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user1@mail.com","password")))
    // THEN bank account successfully retrieved
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content.[0].bankAccountId", is(2)))
        .andExpect(jsonPath("$.content.[0].title", is("My Account")))
        .andExpect(jsonPath("$.content.[0].iban", is("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456")))
        .andExpect(jsonPath("$.content.[0].bic", is("XXXXXXXXxyz")));
  }

  @DisplayName("Remove a bank account")
  @Test
  void removeBankAccountTest() throws Exception {
    // GIVEN User2 have a bank account registered and involved in bank transfer
    // WHEN
    mockMvc.perform(delete("/users/4/bankaccounts/1")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user2@mail.com","password")))
    // THEN Bank account successfully deleted
        .andExpect(status().isNoContent());

    // WHEN
    mockMvc.perform(get("/users/4/bankaccounts")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user2@mail.com","password")))
    // THEN No bank account registred
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));

    // WHEN
    mockMvc.perform(get("/banktransfers/user?id=4")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user2@mail.com","password")))
        // THEN No bank account registred
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

}
