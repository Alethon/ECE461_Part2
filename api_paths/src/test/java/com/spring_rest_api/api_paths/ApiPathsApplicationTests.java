package com.spring_rest_api.api_paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
class ApiPathsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

	// check if the endpoint is get or not
    // @Test
    // void testGetPackages() throws Exception {
    //     mockMvc.perform(MockMvcRequestBuilders.get("/packages"))
    //             .andExpect(MockMvcResultMatchers.status().isOk());
    // }

}
