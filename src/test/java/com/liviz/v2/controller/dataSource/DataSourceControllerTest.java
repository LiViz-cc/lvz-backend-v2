package com.liviz.v2.controller.dataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONObject;
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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// TODO: how to enable transactional for the whole test?
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataSourceControllerTest {
    @Autowired
    MockMvc mockMvc;

    String bearerToken;

    @Value("${spring.test.username}")
    String usernameTest;

    @Value("${spring.test.password}")
    String passwordTest;

    Map<String, Object> dataSourceMap;

    final Integer NUMBER_OF_DATASOURCES = 10;


    @BeforeAll
    void beforeAll() throws Exception {
        login();
        createDataSource();
    }

    private void createDataSource() throws Exception {
        // hashmap to json
        Map<String, Object> jsonPostBody = new HashMap<>();

        // load contents into JSON object
        jsonPostBody.put("name", "bunnyxt's test data source");
        jsonPostBody.put("staticData", "[150, 230, 224, 218, 135, 147, 260]");
        jsonPostBody.put("dataType", "cccc");
        jsonPostBody.put("url", "https://api.weatherapi.com/v1/current.json");
        jsonPostBody.put("dataSourceSlots", new ArrayList<>(Arrays.asList(
                Map.of("name", "q",
                        "slotType", "string",
                        "isOptional", true,
                        "defaultValue", "los ang",
                        "aliasValue", "city"),
                Map.of("name", "key",
                        "slotType", "string",
                        "isOptional", true,
                        "defaultValue", "f41c963f33c244ab94814224222505",
                        "aliasValue", "token")
        )));
//        jsonPostBody.put("dataSourceExample", "");
        jsonPostBody.put("isPublic", true);


        Gson gson = new Gson();

        // post a new DataSource
        MvcResult resultPost = mockMvc.perform(
                        MockMvcRequestBuilders.post("/data_sources")
                                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(jsonPostBody))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // save data source information
        dataSourceMap = gson.fromJson(resultPost.getResponse().getContentAsString(), Map.class);

        // iterate a map
        for (Map.Entry<String, Object> entry : jsonPostBody.entrySet()) {
            if (entry.getKey().equals("dataSourceSlots")) {
                // TODO: nested check for the list of slots
            } else {
                assertNotNull(entry.getValue(), "The value of " + entry.getKey() + " should not be null");
                System.out.println(entry.getKey() + " : " + entry.getValue() + " : " + dataSourceMap.get(entry.getKey()));
                assertNotNull(dataSourceMap.get(entry.getKey()), "key " + entry.getKey() + " not found");
                assertEquals(entry.getValue(),
                        dataSourceMap.get(entry.getKey()),
                        "The " + entry.getKey() + " should be the same." + " Expected: " + entry.getValue() + " Actual: " + dataSourceMap.get(entry.getKey()));
            }
        }


    }

    void login() throws Exception {
        System.out.printf("{\"username\": \"%s\"," +
                "\n\"password\": \"%s\"" +
                "\n}%n", usernameTest, passwordTest);
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
    void test() throws Exception {

    }

    @AfterAll
    void test_deleteProject() throws Exception {
        // delete project by id
        MvcResult resultDelete = mockMvc.perform(MockMvcRequestBuilders
                        .delete(String.format("/data_sources/%s", dataSourceMap.get("id")))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

    }
}