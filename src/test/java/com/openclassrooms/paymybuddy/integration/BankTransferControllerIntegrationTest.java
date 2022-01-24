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
import org.springframework.util.Base64Utils;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BankTransferControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    jsonParam = new JSONObject();
  }

  @DisplayName("Retrieve all bank transfers as Admin")
  @Test
  void getAllBankTransferAsAdminTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/banktransfers")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("admin@mail.com","password")))
    // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.content[0].userId", is(4)))
        .andExpect(jsonPath("$.content[0].bankAccountId", is(1)))
        .andExpect(jsonPath("$.content[0].amount", is(25.0)))
        .andExpect(jsonPath("$.content[0].date", is("2000-01-02 at 00:00" )))
        .andExpect(jsonPath("$.content[0].isIncome", is(false)))
        .andExpect(jsonPath("$.content[0].firstname", is("User2" )))
        .andExpect(jsonPath("$.content[0].lastname", is("test")))
        .andExpect(jsonPath("$.content[0].title", is("Primary Account")));
  }

  @DisplayName("Request a bank transfer between User2 and his bank account")
  @Test
  void requestTransferIntegrationTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",4)
        .put("bankAccountId",1)
        .put("amount","10")
        .put("isIncome",true);

    // WHEN
    mockMvc.perform(post("/banktransfers")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user2@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))
    // THEN Bank transfer successfully performed
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.amount", is(10)))
        .andExpect(jsonPath("$.isIncome", is(true)))
        .andExpect(jsonPath("$.firstname", is("User2" )))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.title", is("Primary Account")));

    // WHEN
    mockMvc.perform(get("/users/4")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user2@mail.com","password")))
    // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wallet", is(10.0)));
  }

}
