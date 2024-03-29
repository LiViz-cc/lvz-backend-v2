package com.liviz.v2.controller.project;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: how to enable transactional for the whole test?
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LinkProjectWithDisplaySchemaTest {
    @Autowired
    MockMvc mockMvc;

    String bearerToken;

    @Value("${spring.test.username}")
    String usernameTest;

    @Value("${spring.test.password}")
    String passwordTest;

    Map<String, String> projectMap;


    @BeforeAll
    void beforeAll() throws Exception {
        login();
        createProject();
    }

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

    void createProject() throws Exception {
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


    MvcResult postDisplaySchema() throws Exception {

        return mockMvc.perform(MockMvcRequestBuilders
                        .post("/display_schemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .content("{\"name\":\"test_display_schema\", " +
                                "\"isPublic\": false," +
                                "\"description\": \"test_description2\"}")
                ).andExpect(status().isCreated())
                .andReturn();
    }

    MvcResult deleteDisplaySchema(String id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                        .delete("/display_schemas/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                ).andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @Order(10)
    void test_linkProjectWithDisplaySchema() throws Exception {
        // create display schema
        MvcResult resultPost = postDisplaySchema();

        JsonNode root = getJsonTree(resultPost);

        String displaySchemaId = root.get("id").asText();

        // link project with display schema
        MvcResult resultLink = mockMvc.perform(MockMvcRequestBuilders
                        .put("/projects/" + projectMap.get("id") + "/display_schema/")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displaySchemaId\":\"" + displaySchemaId + "\"}")
                )
                .andExpect(status().isOk())
                .andReturn();

        // check if the project has the display schema
        MvcResult resultGet = mockMvc.perform(MockMvcRequestBuilders
                        .get("/projects/" + projectMap.get("id"))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displaySchema.id").value(displaySchemaId))
                .andReturn();

        // check if the display schema has the project
        MvcResult resultGet2 = mockMvc.perform(MockMvcRequestBuilders
                        .get("/display_schemas/" + displaySchemaId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.linkedProject.id").value(projectMap.get("id")))
                .andReturn();


        // unlink project with display schema
        MvcResult resultUnlink = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/projects/" + projectMap.get("id") + "/display_schema/")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // check if the project does not have the display schema
        MvcResult resultGet3 = mockMvc.perform(MockMvcRequestBuilders
                        .get("/projects/" + projectMap.get("id"))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displaySchema").doesNotExist())
                .andReturn();

        // check if the display schema does not have the project
        MvcResult resultGet4 = mockMvc.perform(MockMvcRequestBuilders
                        .get("/display_schemas/" + displaySchemaId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.linkedProject").doesNotExist())
                .andReturn();

        // delete display schema
        deleteDisplaySchema(displaySchemaId);
    }

    @Test
    void testDoubleLinking() throws Exception {
        // create display schema
        MvcResult resultPost = postDisplaySchema();

        JsonNode root = getJsonTree(resultPost);

        String displaySchemaId = root.get("id").asText();

        // link project with display schema
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(MockMvcRequestBuilders
                            .put("/projects/" + projectMap.get("id") + "/display_schema/")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"displaySchemaId\":\"" + displaySchemaId + "\"}")
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        }

        // unlink project with display schema twice
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/projects/" + projectMap.get("id") + "/display_schema/")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        }

        // delete display schema
        deleteDisplaySchema(displaySchemaId);
    }


    @AfterAll
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