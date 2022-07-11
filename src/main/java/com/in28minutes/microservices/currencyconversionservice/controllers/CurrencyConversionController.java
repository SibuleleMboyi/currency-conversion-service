package com.in28minutes.microservices.currencyconversionservice.controllers;

import java.math.BigDecimal;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.in28minutes.microservices.currencyconversionservice.configurations.CurrencyExchangeProxy;
import com.in28minutes.microservices.currencyconversionservice.models.CurrencyConversion;

@RestController
public class CurrencyConversionController {

        @Autowired
        private CurrencyExchangeProxy currencyExchangeProxy;

        @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
        public CurrencyConversion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to,
                        @PathVariable BigDecimal quantity) {

                Logger logger = LoggerFactory.getLogger(CurrencyConversion.class);
                HashMap<String, String> uriVariables = new HashMap<>();
                uriVariables.put("from", from);
                uriVariables.put("to", to);
                ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
                                "http://localhost:8000/jpa-currency-exchange/from/USD/to/INR",
                                CurrencyConversion.class, uriVariables);

                CurrencyConversion currencyConversion = responseEntity.getBody();
                currencyConversion.setTotalCalculatedAmount(
                                quantity.multiply(currencyConversion.getConversionMultiple()));

                logger.info(currencyConversion.toString());
                logger.info(quantity.toString());

                // Something is wrong here. Find out why values are a mismatch
                CurrencyConversion currencyConversion2 = new CurrencyConversion(currencyConversion.getId(), from, to,
                                currencyConversion.getConversionMultiple(),

                                currencyConversion.getTotalCalculatedAmount(), quantity,
                                currencyConversion.getEnvironment());

                logger.info(currencyConversion2.toString());
                return currencyConversion2;
        }

        @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
        public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
                        @PathVariable BigDecimal quantity) {

                CurrencyConversion currencyConversion = currencyExchangeProxy.getCurrencyExchangeValueJpa(from, to);

                return new CurrencyConversion(currencyConversion.getId(), from, to,
                                currencyConversion.getConversionMultiple(),

                                currencyConversion.getTotalCalculatedAmount(), quantity,
                                currencyConversion.getEnvironment() + " " + "feign");
        }
}
