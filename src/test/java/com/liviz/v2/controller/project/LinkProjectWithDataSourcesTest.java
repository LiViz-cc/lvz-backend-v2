package com.liviz.v2.controller.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.list.SetUniqueList;
import org.jetbrains.annotations.NotNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: how to enable transactional for the whole test?
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LinkProjectWithDataSourcesTest {
    @Autowired
    MockMvc mockMvc;

    String bearerToken;

    @Value("${spring.test.username}")
    String usernameTest;

    @Value("${spring.test.password}")
    String passwordTest;

    Map<String, String> projectMap;

    final Integer NUMBER_OF_DATASOURCES = 10;


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


    List<String> postDataSources() throws Exception {
        List<String> dataSourceIds = SetUniqueList.setUniqueList(new ArrayList<>());
        for (int i = 0; i < NUMBER_OF_DATASOURCES; i++) {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                            .post("/data_sources")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\n" +
                                    "    \"name\": \"bunnyxt's test data source\",\n" +
                                    "    \"staticData\": \"[150, 230, 224, 218, 135, 147, 260]\",\n" +
                                    "    \"dataType\": \"cccc\",\n" +
                                    "    \"url\": \"https://api.weatherapi.com/v1/current.json\",\n" +
                                    "    \"dataSourceSlots\": [\n" +
                                    "        {\n" +
                                    "            \"name\": \"q\",\n" +
                                    "            \"slotType\": \"string\",\n" +
                                    "            \"isOptional\": true,\n" +
                                    "            \"defaultValue\": \"los ang\",\n" +
                                    "            \"aliasValue\": \"city\"\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "            \"name\": \"key\",\n" +
                                    "            \"slotType\": \"string\",\n" +
                                    "            \"isOptional\": true,\n" +
                                    "            \"defaultValue\": \"f41c963f33c244ab94814224222505\",\n" +
                                    "            \"aliasValue\": \"token\"\n" +
                                    "        }\n" +
                                    "    ],\n" +
                                    "    \"dataSourceExample\": null,\n" +
                                    "    \"isPublic\": true\n" +
                                    "}")
                    )
                    .andExpect(status().isCreated())
                    .andReturn();

            // parse the response to get the id
            JsonNode root = getJsonTree(result);
            String dataSourceId = root.get("id").asText();

            // add the id to the list
            dataSourceIds.add(dataSourceId);
        }

        return dataSourceIds;
    }

    void deleteDataSources(List<String> ids) throws Exception {
        for (String id : ids) {
            mockMvc.perform(MockMvcRequestBuilders
                            .delete("/data_sources/" + id)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNoContent())
                    .andReturn();
        }
    }

    @Test
    @Order(10)
    void test_linkProjectWithDisplaySchema() throws Exception {
        // create data sources
        List<String> dataSourceIds = postAndTestDataSourcesLinkage();

        // unlink the project with the data sources
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/projects/" + projectMap.get("id") + "/data_sources")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"dataSourceIds\": " +
                                "[\"" + String.join("\",\"", dataSourceIds) + "\"]" +
                                "\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andReturn();

        // check the linkage removal from the project to the data sources
        MvcResult resultAfterUnlink = mockMvc.perform(MockMvcRequestBuilders
                        .get("/projects/" + projectMap.get("id"))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootAfterUnlink = getJsonTree(resultAfterUnlink);
        JsonNode dataSourcesAfterUnlink = rootAfterUnlink.get("dataSources");
        assertEquals(0, dataSourcesAfterUnlink.size());

        for (String dataSourceId : dataSourceIds) {
            MvcResult resultAfterUnlinkDataSource = mockMvc.perform(MockMvcRequestBuilders
                            .get("/data_sources/" + dataSourceId)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            JsonNode rootAfterUnlinkDataSource = getJsonTree(resultAfterUnlinkDataSource);

            JsonNode projectsAfterUnlinkDataSource = rootAfterUnlinkDataSource.get("projects");
            assertEquals(0, projectsAfterUnlinkDataSource.size());
        }

        // delete the data sources
        deleteDataSources(dataSourceIds);
    }

    @Test
    @Order(20)
    void test_unlinkProjectWithDisplaySchemaByDeletingDataSources() throws Exception {
        List<String> dataSourceIds = postAndTestDataSourcesLinkage();

        // delete the data sources
        deleteDataSources(dataSourceIds);

        // check the linkage removal from the project to the data sources
        MvcResult resultAfterUnlink = mockMvc.perform(MockMvcRequestBuilders
                        .get("/projects/" + projectMap.get("id"))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootAfterUnlink = getJsonTree(resultAfterUnlink);
        JsonNode dataSourcesAfterUnlink = rootAfterUnlink.get("dataSources");
        assertEquals(0, dataSourcesAfterUnlink.size());
    }

    @NotNull
    private List<String> postAndTestDataSourcesLinkage() throws Exception {
        // create data sources
        List<String> dataSourceIds = postDataSources();

        // link the project with the data sources
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/projects/" + projectMap.get("id") + "/data_sources")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"dataSourceIds\": " +
                                "[\"" + String.join("\",\"", dataSourceIds) + "\"]" +
                                "\n" +
                                "}")
                )
                .andExpect(status().isOk())
                .andReturn();

        // link the data sources with the project again
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/projects/" + projectMap.get("id") + "/data_sources")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"dataSourceIds\": " +
                                "[\"" + String.join("\",\"", dataSourceIds) + "\"]" +
                                "\n" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // check the linkage from the project to the data sources
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/projects/" + projectMap.get("id"))
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = getJsonTree(result);
        JsonNode dataSources = root.get("dataSources");
        assertEquals(NUMBER_OF_DATASOURCES, dataSources.size());

        for (JsonNode dataSource : dataSources) {
            String dataSourceId = dataSource.get("id").asText();
            assertTrue(dataSourceIds.contains(dataSourceId));
        }

        for (String dataSourceId : dataSourceIds) {
            MvcResult resultGetDataSource = mockMvc.perform(MockMvcRequestBuilders
                            .get("/data_sources/" + dataSourceId)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

            JsonNode rootDataSource = getJsonTree(resultGetDataSource);

            for (JsonNode project : rootDataSource.get("projects")) {
                String projectId = project.get("id").asText();
                assertEquals(projectMap.get("id"), projectId);
            }

        }
        return dataSourceIds;
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