package com.TeamC.Eventiefy.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${mailgun.api.key}")
    private String mailgunApiKey;

    @Value("${mailgun.domain}")
    private String mailgunDomain;

    public void sendEmail(String to, String subject, String body) throws IOException {
        try {
            HttpResponse<String> request = Unirest.post("https://api.mailgun.net/v3/" + mailgunDomain + "/messages")
                    .basicAuth("api", mailgunApiKey)
                    .queryString("from", "no-reply@" + mailgunDomain)
                    .queryString("to", to)
                    .queryString("subject", subject)
                    .queryString("text", body)
                    .asString();

            System.out.println(request.getStatus());
            System.out.println(request.getBody());
        } catch (UnirestException e) {
            throw new IOException("Failed to send email", e);
        }
    }
}
