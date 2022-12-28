package com.liviz.v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Value("${spring.test.bearer-token}")
    String bearerToken;

    Map<String, String> projectMap;

    @BeforeAll
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

}