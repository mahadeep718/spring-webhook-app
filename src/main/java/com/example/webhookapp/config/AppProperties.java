package com.example.webhookapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name;
    private String regNo;
    private String email;
    private Endpoints endpoints = new Endpoints();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public static class Endpoints {
        private String generateWebhook;
        private String fallbackWebhook;

        public String getGenerateWebhook() {
            return generateWebhook;
        }

        public void setGenerateWebhook(String generateWebhook) {
            this.generateWebhook = generateWebhook;
        }

        public String getFallbackWebhook() {
            return fallbackWebhook;
        }

        public void setFallbackWebhook(String fallbackWebhook) {
            this.fallbackWebhook = fallbackWebhook;
        }
    }
}