# Spring Webhook App

Spring Boot app that generates a webhook, solves a SQL problem based on regNo parity, stores the final SQL, and submits it using a JWT — configured for Mahadeep (`regNo: 22bce8813`).

## How It Works
- On startup, the app sends a POST request to `generateWebhook/JAVA` with your name, regNo, and email.
- It receives a `webhook` URL and `accessToken` (JWT) in the response.
- Based on the last two digits of `regNo` (odd/even), it loads an SQL query from `src/main/resources/sql/question1.sql` or `question2.sql`.
- It writes the final SQL to `target/finalQuery.sql` and posts `{"finalQuery": "..."}` to the webhook with `Authorization: <accessToken>`.

## Configuration
`src/main/resources/application.yml` is set for plagiarism check to your identity:

```yaml
app:
  name: "Mahadeep"
  regNo: "22bce8813"     # last two digits 13 → odd → question1.sql
  email: "mahadeep@example.com"
  endpoints:
    generateWebhook: "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA"
    fallbackWebhook: "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA"
```

Place your actual SQL solutions in:
- `src/main/resources/sql/question1.sql` (odd)
- `src/main/resources/sql/question2.sql` (even)

## Build and Run

Requires Java 17 and Maven.

```bash
mvn -q -DskipTests package
java -jar target/spring-webhook-app-1.0.0.jar
```

The app runs once, performs the workflow, and exits. Logs include the submission status. The final SQL is saved to `target/finalQuery.sql`.

## Public JAR Link
- Raw downloadable JAR: `https://raw.githubusercontent.com/mahadeep718/spring-webhook-app/main/target/spring-webhook-app-1.0.0.jar`

## Submission Checklist
- Repo pushed publicly under your account.
- Built JAR included in `target/`.
- Public RAW link (above) is directly downloadable.

## Notes
- `Authorization` header uses the JWT returned by `generateWebhook/JAVA` as-is (add `Bearer ` prefix only if the API requires it).
- If `webhook` is missing in the response, the app falls back to `https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA`.