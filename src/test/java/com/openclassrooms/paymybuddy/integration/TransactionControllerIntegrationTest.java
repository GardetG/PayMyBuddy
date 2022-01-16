package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.utils.JsonParser;
import org.json.JSONObject;
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
class TransactionControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private String encodeCredentials(String username, String password) {
    String credentials = String.format("%s:%s", username, password);
    return "Basic " + Base64Utils.encodeToString(credentials.getBytes());
  }

  @Test
  void getAllBankTransferAsAdminIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/transactions")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("admin@mail.com","password")))

        // THEN
        // Check response
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.content[0].emitterId", is(2)))
        .andExpect(jsonPath("$.content[0].receiverId", is(3)))
        .andExpect(jsonPath("$.content[0].description", is("Gift for a friend")))
        .andExpect(jsonPath("$.content[0].amount", is(25.0)))
        .andExpect(jsonPath("$.content[0].emitterFirstname", is("test")))
        .andExpect(jsonPath("$.content[0].emitterLastname", is("test")))
        .andExpect(jsonPath("$.content[0].receiverFirstname", is("test2")))
        .andExpect(jsonPath("$.content[0].receiverLastname", is("test2")));
  }

  @Test
  void requestTransactionIntegrationTest() throws Exception {
    // GIVEN
    JSONObject jsonParam = new JSONObject();
    jsonParam.put("emitterId",3).put("receiverId",2)
        .put("description","Transaction test").put("amount",10);

    // WHEN
    mockMvc.perform(post("/transactions")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user2@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        // Check response
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.emitterId", is(3)))
        .andExpect(jsonPath("$.receiverId", is(2)))
        .andExpect(jsonPath("$.description", is("Transaction test")))
        .andExpect(jsonPath("$.amount", is(10)))
        .andExpect(jsonPath("$.emitterFirstname", is("test2")))
        .andExpect(jsonPath("$.emitterLastname", is("test2")))
        .andExpect(jsonPath("$.receiverFirstname", is("test")))
        .andExpect(jsonPath("$.receiverLastname", is("test")));

    //Check Balance
    mockMvc.perform(get("/users/2")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("admin@mail.com","password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wallet", is(10.0)));

    mockMvc.perform(get("/users/3")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("admin@mail.com","password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wallet", is(89.95)));
  }
}
