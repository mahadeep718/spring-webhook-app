package com.example.webhookapp.service;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class QuerySolver {

    public String solveQuery(String regNo) throws IOException {
        int lastTwo = parseLastTwoDigits(regNo);
        boolean isOdd = (lastTwo % 2) != 0;
        String resourcePath = isOdd ? "sql/question1.sql" : "sql/question2.sql";

        ClassPathResource resource = new ClassPathResource(resourcePath);
        byte[] bytes = resource.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private int parseLastTwoDigits(String regNo) {
        String digits = regNo.replaceAll("[^0-9]", "");
        if (digits.length() >= 2) {
            String lastTwo = digits.substring(digits.length() - 2);
            return Integer.parseInt(lastTwo);
        }
        // Fallback: if not enough digits, treat as odd
        return 1;
    }
}