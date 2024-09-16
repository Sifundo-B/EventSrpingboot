package com.TeamC.Eventiefy.config;

import com.paypal.base.rest.APIContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {
    @Bean
    public APIContext apiContext() {
        String clientId = "AUF6coeuIS_VGNqX14mMAZ-ZU8vJrCf-4YSmyuokTlGyNcFCoqux29mDWOaW64wGggoonMqjARmAUaYC";
        String clientSecret = "EAprwSiMWgIe1VVAFt57rcsyWirNVXfaKh2CcHG2RyuY5tgq2ODYHQenEoM8XqNsixM3P5EELP8qQHNn";
        return new APIContext(clientId, clientSecret, "sandbox");
    }
}
