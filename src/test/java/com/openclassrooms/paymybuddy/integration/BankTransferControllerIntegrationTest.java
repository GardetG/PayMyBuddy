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
class BankTransferControllerIntegrationTest {

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
    mockMvc.perform(get("/banktransfers")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("admin@mail.com","password")))

        // THEN
        // Check response
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.content[0].userId", is(2)))
        .andExpect(jsonPath("$.content[0].bankAccountId", is(1)))
        .andExpect(jsonPath("$.content[0].amount", is(25.0)))
        .andExpect(jsonPath("$.content[0].date", is("2022-01-07 at 20:34" )))
        .andExpect(jsonPath("$.content[0].income", is(false)))
        .andExpect(jsonPath("$.content[0].firstname", is("test" )))
        .andExpect(jsonPath("$.content[0].lastname", is("test")))
        .andExpect(jsonPath("$.content[0].title", is("Primary Account")));
  }

  @Test
  void requestTransferIntegrationTest() throws Exception {
    // GIVEN
    JSONObject jsonParam = new JSONObject();
    jsonParam.put("userId",2).put("bankAccountId",1)
        .put("amount","10").put("income",true);

    // WHEN
    mockMvc.perform(post("/banktransfers")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        // Check response
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.amount", is(10)))
        .andExpect(jsonPath("$.income", is(true)))
        .andExpect(jsonPath("$.firstname", is("test" )))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.title", is("Primary Account")));

    //Check Balance
    mockMvc.perform(get("/users/2")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user@mail.com","password")))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.wallet", is(10.0)));

  }
}
