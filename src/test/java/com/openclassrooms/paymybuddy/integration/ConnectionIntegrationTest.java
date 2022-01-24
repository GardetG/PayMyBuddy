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
class ConnectionIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    jsonParam = new JSONObject();
  }

  @DisplayName("Add a connection")
  @Test
  void addConnectionTest() throws Exception {
    // GIVEN User1 have a connection with User2 and add a new one
    jsonParam.put("email", "johndoe@mail.com");

    // WHEN
    mockMvc.perform(post("/users/3/connections")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user1@mail.com", "password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))
        // THEN connection successfully added
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.connectionId", is(2)));

    // WHEN
    mockMvc.perform(get("/users/3/connections")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user1@mail.com", "password")))
        //THEN connections successfully retrieved
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content.[0].connectionId", is(2)))
        .andExpect(jsonPath("$.content.[0].firstname", is("John")))
        .andExpect(jsonPath("$.content.[0].lastname", is("Doe")));
  }

  @DisplayName("Remove a connection")
  @Test
  void removeBankAccountTest() throws Exception {
    // GIVEN User2 have a connection registered
    // WHEN
    mockMvc.perform(delete("/users/4/connections/3")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user2@mail.com", "password")))
        // THEN Connection successfully deleted
        .andExpect(status().isNoContent());

    // WHEN
    mockMvc.perform(get("/users/4/connections")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user2@mail.com", "password")))
        // THEN No connection registered
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

}