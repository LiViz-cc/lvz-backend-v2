package com.liviz.v2.controller.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: how to enable transactional for the whole test?
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CloneProjectTest {
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
        System.out.println(String.format("{\"username\": \"%s\"," +
                "\n\"password\": \"%s\"" +
                "\n}", usernameTest, passwordTest));
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

        JsonNode root = getJsonTree(result);

        // Extract the value you want from the JSON
        String yourValue = root.get("token").get("jwtToken").asText();

        bearerToken = "Bearer " + yourValue;

    }

    private static JsonNode getJsonTree(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        // Get the response body as a string
        String responseBody = result.getResponse().getContentAsString();

        // Parse the response body as JSON
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(responseBody);
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

    @Order(2)
    @Test
    void test_cloneProject() throws Exception {
        // edit project by id
        MvcResult resultEdit = mockMvc.perform(MockMvcRequestBuilders
                        .post(String.format("/projects/clone/?id=%s", projectMap.get("id")))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("test_description"))
                .andReturn();


        JsonNode root = getJsonTree(resultEdit);
        String newId = root.get("id").asText();
        assertNotEquals(newId, projectMap.get("id"));


        // delete cloned project
        MvcResult resultDelete = mockMvc.perform(MockMvcRequestBuilders
                        .delete(String.format("/projects/%s", newId))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
    }


    @Test
    @Order(999)
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
}