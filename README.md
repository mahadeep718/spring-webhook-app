# Spring Webhook App

Build a Spring Boot app that, on startup, generates a webhook, solves a SQL problem based on the `regNo` parity, stores the final SQL, and submits it using a JWT.

## How it works
- On startup, the app sends a POST request to `generateWebhook/JAVA` with your name, regNo, and email.
- It receives a `webhook` URL and `accessToken` (JWT) in the response.
- Based on the last two digits of `regNo` (odd/even), it loads an SQL query from `src/main/resources/sql/question1.sql` or `question2.sql`.
- It writes the final SQL to `target/finalQuery.sql` and posts `{"finalQuery": "..."}` to the webhook with `Authorization: <accessToken>`.

## Configuration
Edit `src/main/resources/application.yml`:

```yaml
app:
  name: "John Doe"
  regNo: "REG12347"     # controls odd/even selection
  email: "john@example.com"
  endpoints:
    generateWebhook: "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA"
    fallbackWebhook: "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA"
```

Place your actual SQL solutions in:
- `src/main/resources/sql/question1.sql` (odd)
- `src/main/resources/sql/question2.sql` (even)

Note: If the problem statements are inaccessible (e.g., broken links), keep placeholders and replace with your actual final SQL later.

## Build and Run

Requires Java 17 and Maven.

```bash
mvn -q -DskipTests package
java -jar target/spring-webhook-app-1.0.0.jar
```

The app runs once, performs the workflow, and exits. Logs include the submission status. The final SQL is saved to `target/finalQuery.sql`.

## Submission Checklist
- Push this repo to a public GitHub repository.
- Include the built JAR in `target/` and commit or attach it as a release asset.
- Provide the RAW downloadable GitHub link to the JAR, e.g.:
  - `https://raw.githubusercontent.com/<your-username>/<repo>/main/target/spring-webhook-app-1.0.0.jar`
- Provide a public downloadable link to the JAR (GitHub release asset or raw link).

## Notes
- Authorization header uses the JWT token returned by `generateWebhook/JAVA` as-is (no `Bearer` prefix unless explicitly required by the API).
- If `webhook` is missing in the response, the app falls back to `https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA`.