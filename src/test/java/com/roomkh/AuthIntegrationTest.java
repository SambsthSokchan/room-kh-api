package com.roomkh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private static String token;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("1. Register Student: POST /api/auth/register")
    void testRegisterStudent() throws Exception {
        String uniqueEmail = "dara_" + System.currentTimeMillis() + "@rupp.edu.kh";
        String uniquePhone = "012" + (int)(Math.random() * 900000 + 100000);

        String json = """
            {
              "fullName": "Sok Dara",
              "email": "%s",
              "phoneNumber": "%s",
              "password": "password123",
              "confirmPassword": "password123",
              "userSegment": "STUDENT",
              "language": "KHMER",
              "university": "Royal University of Phnom Penh",
              "faculty": "Computer Science",
              "studyYear": 3
            }
            """.formatted(uniqueEmail, uniquePhone);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.fullName").value("Sok Dara"))
                .andExpect(jsonPath("$.data.email").value(uniqueEmail))
                .andExpect(jsonPath("$.data.role").value("RENTER"))
                .andExpect(jsonPath("$.data.userSegment").value("STUDENT"));

        // Login with this registered user
        String loginJson = """
            {
              "email": "%s",
              "password": "password123"
            }
            """.formatted(uniqueEmail);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andReturn();

        JsonNode responseNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        token = responseNode.path("data").path("accessToken").asText();
        assertThat(token).isNotBlank();
    }

    @Test
    @Order(2)
    @DisplayName("2. Unauthenticated Profile: GET /api/users/me without token -> 403 Forbidden")
    void testProtectedProfileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @DisplayName("3. Authenticated Profile: GET /api/users/me with Bearer token -> 200 OK")
    void testProtectedProfileWithToken() throws Exception {
        assertThat(token).isNotBlank();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile loaded"))
                .andExpect(jsonPath("$.data.fullName").value("Sok Dara"))
                .andExpect(jsonPath("$.data.role").value("RENTER"))
                .andExpect(jsonPath("$.data.userSegment").value("STUDENT"));
    }
}
