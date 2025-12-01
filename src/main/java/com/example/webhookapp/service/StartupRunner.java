package com.example.webhookapp.service;

import com.example.webhookapp.config.AppProperties;
import com.example.webhookapp.model.GenerateWebhookRequest;
import com.example.webhookapp.model.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final RestTemplate restTemplate;
    private final AppProperties props;
    private final QuerySolver querySolver = new QuerySolver();

    public StartupRunner(RestTemplate restTemplate, AppProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1) Generate webhook
        String generateUrl = props.getEndpoints().getGenerateWebhook();
        GenerateWebhookRequest reqBody = new GenerateWebhookRequest(props.getName(), props.getRegNo(), props.getEmail());

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenerateWebhookRequest> requestEntity = new HttpEntity<>(reqBody, reqHeaders);

        log.info("Requesting webhook from {}", generateUrl);
        ResponseEntity<GenerateWebhookResponse> generateResp = restTemplate.postForEntity(generateUrl, requestEntity, GenerateWebhookResponse.class);

        if (!generateResp.getStatusCode().is2xxSuccessful() || generateResp.getBody() == null) {
            throw new IllegalStateException("Failed to generate webhook: status=" + generateResp.getStatusCode());
        }

        GenerateWebhookResponse payload = generateResp.getBody();
        String webhookUrl = payload.getWebhook() != null && !payload.getWebhook().isBlank()
                ? payload.getWebhook()
                : props.getEndpoints().getFallbackWebhook();
        String accessToken = payload.getAccessToken();

        log.info("Webhook URL obtained: {}", webhookUrl);

        // 2) Solve SQL based on regNo parity
        String finalQuery;
        try {
            finalQuery = querySolver.solveQuery(props.getRegNo());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load SQL query from resources", e);
        }

        // 3) Store result locally
        Path outDir = Path.of("target");
        Files.createDirectories(outDir);
        Path outFile = outDir.resolve("finalQuery.sql");
        Files.writeString(outFile, finalQuery);
        log.info("Final SQL query stored at {}", outFile.toAbsolutePath());

        // 4) Submit solution to webhook using JWT in Authorization header
        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null && !accessToken.isBlank()) {
            submitHeaders.set("Authorization", accessToken);
        }
        HttpEntity<Map<String, String>> submitEntity = new HttpEntity<>(Map.of("finalQuery", finalQuery), submitHeaders);

        log.info("Submitting final SQL query to webhook");
        ResponseEntity<String> submitResp = restTemplate.postForEntity(webhookUrl, submitEntity, String.class);
        log.info("Submission status: {}", submitResp.getStatusCode());
        if (submitResp.getBody() != null) {
            log.info("Submission response body: {}", submitResp.getBody());
        }
    }
}