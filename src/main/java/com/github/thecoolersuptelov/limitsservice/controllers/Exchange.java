package com.github.thecoolersuptelov.limitsservice.controllers;

import com.github.thecoolersuptelov.limitsservice.Domain.CurrencyExchange;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/currency-exchange/")
public class Exchange {
    private final Environment environment;

    public Exchange(Environment environment) {
        this.environment = environment;
    }

    @GetMapping(value = "from/{fromCurrency}/to/{toCurrency}")
    public CurrencyExchange retriveExchangeValue(@PathVariable String fromCurrency, @PathVariable String toCurrency) {
        return new CurrencyExchange(UUID.randomUUID(), fromCurrency, toCurrency, 100l, environment.getProperty("local.server.port"));
    }
}
