package com.github.thecoolersuptelov.limitsservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExchangeControllerTest {
    public MockMvc mockMvc;

    public ExchangeControllerTest(WebApplicationContext webAppContext) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void testExchangeController() throws Exception {

        var req = mockMvc.perform(get("/currency-exchange/from/USD/to/INR")).andReturn().getResponse();
        assertEquals(req.getStatus(), 200);
    }
}
