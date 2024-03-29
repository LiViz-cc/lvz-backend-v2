package com.liviz.v2.controller.project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO: add tests for all controller methods
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectControllerTest {
    @Autowired
    MockMvc mockMvc;

    String bearerToken;

    @Value("${spring.test.username}")
    String usernameTest;

    @Value("${spring.test.password}")
    String passwordTest;

    Map<String, String> projectMap;

    @BeforeAll
    void login() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"username\": \"%s\"," +
                                "\n\"password\": \"%s\"" +
                                "\n}", usernameTest, passwordTest))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token.jwtToken").exists())
                .andReturn(); // Get the full MvcResult object

        // Get the response body as a string
        String responseBody = result.getResponse().getContentAsString();

        // Parse the response body as JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);

        // Extract the value you want from the JSON
        String yourValue = root.get("token").get("jwtToken").asText();

        bearerToken = "Bearer " + yourValue;

    }

    @Test
    @Order(0)
    void test_postProject() throws Exception {
        // test post api
        MvcResult resultPost = mockMvc.perform(MockMvcRequestBuilders
                        .post("/projects")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", " +
                                "\"isPublic\": true," +
                                "\"description\": \"test_description\"}")
                )
                .andExpect(status().isCreated())
//                .andDo(print())
                .andReturn();

        String responseFromPost = resultPost.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        projectMap = objectMapper.readValue(responseFromPost, Map.class);
    }

    @Test
    @Order(1)
    void test_getProject() throws Exception {

        // get project by id
        MvcResult resultGet = mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/projects/%s", projectMap.get("id")))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
//                .andDo(print())
                .andExpect(jsonPath("$.id").value(projectMap.get("id")))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.name").value("test"))
                .andReturn();

    }

    @Test
    @Order(2)
    void test_deleteProject() throws Exception {
        // delete project by id
        MvcResult resultDelete = mockMvc.perform(MockMvcRequestBuilders
                        .delete(String.format("/projects/%s", projectMap.get("id")))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

    }


    @Test
    @Order(3)
    void test_getProjectNotFound() throws Exception {
        // get project by id
        MvcResult resultGet = mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("/projects/%s", projectMap.get("id")))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    @Order(100)
    void test_postProjectBadRequest() throws Exception {
        // test post api
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/projects")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", " +
                                "\"isPublic\": true}")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("description: must not be null")))
        ;

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/projects")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", " +
                                "\"description\": \"test_description\"}")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("isPublic: must not be null")));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/projects")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"isPublic\": true," +
                                "\"description\": \"test_description\"}")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name: must not be blank")));


    }
}