package com.github.thecoolersuptelov.limitsservice.repository;

import com.github.thecoolersuptelov.limitsservice.Domain.CurrencyExchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange,UUID> {
    CurrencyExchange findByFromCurrencyEqualsIgnoreCaseAndToCurrencyEqualsIgnoreCase(String fromCurrency, String toCurrency);

}
