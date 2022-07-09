package com.github.thecoolersuptelov.limitsservice.controllers;

import com.github.thecoolersuptelov.limitsservice.Domain.CurrencyExchange;
import com.github.thecoolersuptelov.limitsservice.repository.CurrencyExchangeRepository;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/currency-exchange/")
public class Exchange {
    private final Environment environment;
    private final CurrencyExchangeRepository repository;

    public Exchange(Environment environment, CurrencyExchangeRepository repository) {
        this.repository = repository;
        this.environment = environment;
    }

    @GetMapping(value = "from/{fromCurrency}/to/{toCurrency}")
    public CurrencyExchange retriveExchangeValue(@PathVariable @NonNull String fromCurrency, @PathVariable @NonNull String toCurrency) {
        var currencyExchange = repository.findByFromCurrencyEqualsIgnoreCaseAndToCurrencyEqualsIgnoreCase(fromCurrency,toCurrency);
        return currencyExchange.setEnviroment(environment.getProperty("local.server.port"));
    }
    @PostMapping(value = "from/{fromCurrency}/to/{toCurrency}")
    public CurrencyExchange retriveExchangeValue(@PathVariable @NonNull String fromCurrency, @PathVariable @NonNull String toCurrency,@RequestBody @NonNull Long value) {
        return repository.save(new CurrencyExchange(fromCurrency, toCurrency, value, environment.getProperty("local.server.port")));
    }
}
