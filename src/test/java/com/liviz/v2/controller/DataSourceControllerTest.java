package com.liviz.v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DataSourceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Value("${spring.test.bearer-token}")
    String bearerToken;

    @Test
    void getDataSourceById() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/data_sources/63abafc39a2b087e8995b26d")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())
                .andDo(print())
//                .andExpect(jsonPath("$.name").value("63abafc39a2b087e8995b26d"))
                .andReturn();

        System.out.println("here");
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void createDataSource() {
    }
}