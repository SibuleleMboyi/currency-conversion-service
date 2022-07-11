package com.in28minutes.microservices.currencyconversionservice.configurations;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.in28minutes.microservices.currencyconversionservice.models.CurrencyConversion;

// We must use a name of the application we want to call and it's URL(you may shorten it like below).
// This name is configured in the application.properties of the application we are calling.
// configured in this manner,
// spring.application.name=currency-exchange
@FeignClient(name = "currency-exchange", url = "localhost:8000")
public interface CurrencyExchangeProxy {

    @GetMapping("/jpa-currency-exchange/from/{from}/to/{to}")
    public CurrencyConversion getCurrencyExchangeValueJpa(@PathVariable String from, @PathVariable String to);

}
