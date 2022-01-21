package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
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
class TransactionIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    jsonParam = new JSONObject();
  }

  @DisplayName("Retrieve all transactions as Admin")
  @Test
  void getAllBankTransferAsAdminTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/transactions")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("admin@mail.com","password")))
    // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.content[0].emitterId", is(3)))
        .andExpect(jsonPath("$.content[0].receiverId", is(2)))
        .andExpect(jsonPath("$.content[0].description", is("Gift for a friend")))
        .andExpect(jsonPath("$.content[0].amount", is(25.0)))
        .andExpect(jsonPath("$.content[0].date", is("2000-01-02 at 00:00")))
        .andExpect(jsonPath("$.content[0].emitterFirstname", is("User2")))
        .andExpect(jsonPath("$.content[0].emitterLastname", is("test")))
        .andExpect(jsonPath("$.content[0].receiverFirstname", is("User1")))
        .andExpect(jsonPath("$.content[0].receiverLastname", is("test")));
  }

  @DisplayName("Request a transactions between User 1 and User 2")
  @Test
  void requestTransactionTest() throws Exception {
    // GIVEN
    jsonParam.put("emitterId",2)
        .put("receiverId",3)
        .put("description","Transaction test")
        .put("amount",10);

    // WHEN
    mockMvc.perform(post("/transactions")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user1@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

    // THEN Transaction successfully performed
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.emitterId", is(2)))
        .andExpect(jsonPath("$.receiverId", is(3)))
        .andExpect(jsonPath("$.description", is("Transaction test")))
        .andExpect(jsonPath("$.amount", is(10)))
        .andExpect(jsonPath("$.emitterFirstname", is("User1")))
        .andExpect(jsonPath("$.emitterLastname", is("test")))
        .andExpect(jsonPath("$.receiverFirstname", is("User2")))
        .andExpect(jsonPath("$.receiverLastname", is("test")));

    // WHEN
    mockMvc.perform(get("/users/2")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user1@mail.com","password")))
    // THEN User 1 successfully debited of the amount plus the fare
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wallet", is(89.95)));

    // WHEN
    mockMvc.perform(get("/users/3")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user2@mail.com","password")))
    // THEN User 2 successfully credited of the amount
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wallet", is(10.0)));
  }
}
