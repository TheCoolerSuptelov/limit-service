package com.github.thecoolersuptelov.limitsservice.Domain;

import java.util.UUID;

public class CurrencyExchange {
    private UUID id;
    private String fromCurrency;
    private String toCurrency;
    private Long convertionMultiple;
    private String enviroment;
    public CurrencyExchange(UUID id, String fromCurrency, String toCurrency, Long convertionMultiple, String enviroment) {
        this.id = id;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.convertionMultiple = convertionMultiple;
        this.enviroment = enviroment;
    }

    public CurrencyExchange(String fromCurrency, String toCurrency, Long convertionMultiple, String enviroment) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.convertionMultiple = convertionMultiple;
        this.enviroment = enviroment;
    }

    public CurrencyExchange() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public Long getConvertionMultiple() {
        return convertionMultiple;
    }

    public void setConvertionMultiple(Long convertionMultiple) {
        this.convertionMultiple = convertionMultiple;
    }

    public String getEnviroment() {
        return enviroment;
    }

    public void setEnviroment(String enviroment) {
        this.enviroment = enviroment;
    }
}
