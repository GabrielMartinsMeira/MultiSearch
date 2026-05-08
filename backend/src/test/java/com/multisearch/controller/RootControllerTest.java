package com.multisearch.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RootController.class)
@DisplayName("RootController - Redirect Unit Tests")
class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / → redirects with 302 Found to /api/all")
    void rootEndpoint_redirectsToApiAll() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound()) // 302 explicitly — not 301, 307, or 308
                .andExpect(redirectedUrl("/api/all"));
    }

    @Test
    @DisplayName("POST / → must reject with client error (only GET is allowed)")
    void rootEndpoint_postMethod_isRejected() throws Exception {
        mockMvc.perform(post("/"))
                .andExpect(status().is4xxClientError());
    }
}