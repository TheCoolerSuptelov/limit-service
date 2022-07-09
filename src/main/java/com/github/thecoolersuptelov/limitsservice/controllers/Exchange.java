package com.github.thecoolersuptelov.limitsservice.controllers;

import com.github.thecoolersuptelov.limitsservice.Domain.CurrencyExchange;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/currency-exchange/")
public class Exchange {
    @GetMapping(value = "from/{fromCurrency}/to/{toCurrency}")
    public CurrencyExchange retriveExchangeValue(@PathVariable String fromCurrency, @PathVariable String toCurrency) {
        return new CurrencyExchange(UUID.randomUUID(), fromCurrency, toCurrency, 100l,"osos");
    }
}
