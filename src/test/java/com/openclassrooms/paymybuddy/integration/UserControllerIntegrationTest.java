package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserSubscriptionDto;
import com.openclassrooms.paymybuddy.utils.JsonParser;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void subscribeIntegrationTest() throws Exception {
    // GIVEN
    UserSubscriptionDto subscriptionDto = new UserSubscriptionDto("test","test", "test@mail.com","12345678");

    // WHEN
    mockMvc.perform(post("/subscribe")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(subscriptionDto)))

        // THEN
        // Check response
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", is(3)));
    // Check that a person has been added
    mockMvc.perform(get("/users/3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)));
  }

  @Test
  void updateTest() throws Exception {
    // GIVEN
    UserInfoDto updatedUser = new UserInfoDto(2,"update","test", "update@mail.com", null);

    // WHEN
    mockMvc.perform(put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(updatedUser)))

        // THEN
        // Check response
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(2)));
    // Check that a person has been added
    mockMvc.perform(get("/users/2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname", is("update")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("update@mail.com")))
        .andExpect(jsonPath("$.wallet", is(100)));
  }

  @Test
  void deleteIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/1"))

        // THEN
        // Check response
        .andExpect(status().isNoContent());
    // Check that the user is deleted
    mockMvc.perform(get("/users/1"))
        .andExpect(status().isNotFound());
  }
}
