package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.utils.JsonParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BankAccountControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private String encodeCredentials(String username, String password) {
    String credentials = String.format("%s:%s", username, password);
    return "Basic " + Base64Utils.encodeToString(credentials.getBytes());
  }

  @Test
  void addBankAccountIntegrationTest() throws Exception {
    // GIVEN
    BankAccountDto accountToCreate = new BankAccountDto(0, "My Account","1234567890abcdefghijklmnopqrstu456","12345678xyz");
    // WHEN
    mockMvc.perform(post("/users/2/bankaccounts")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(accountToCreate)))

        // THEN
        // Check response
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasSize(1)));

    // Check that user successfully registered
    mockMvc.perform(get("/users/2/bankaccounts")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user@mail.com","password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].bankAccountId", is(2)))
        .andExpect(jsonPath("$[0].title", is("My Account")))
        .andExpect(jsonPath("$[0].iban", is("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX456")))
        .andExpect(jsonPath("$[0].bic", is("XXXXXXXXxyz")));
  }

  @Test
  void deleteAccountIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/3/bankaccounts/1")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user2@mail.com","password")))

        // THEN
        // Check response
        .andExpect(status().isNoContent());

    // Check that user successfully registered
    mockMvc.perform(get("/users/3/bankaccounts")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user2@mail.com","password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

}
