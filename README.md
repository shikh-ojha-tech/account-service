# Account Service

Multi-currency accounts, balances, IN/OUT transactions, history, and RabbitMQ events.

Repo: https://github.com/shikh-ojha-tech/account-service

## Live demo

Codespaces: https://codespaces.new/shikh-ojha-tech/account-service?quickstart=1

## Docker

```bash
docker compose up --build
```

Swagger: http://localhost:8080/swagger-ui.html

## Auth

Send header `X-API-Key`.

- Local / Compose: `demo-api-key` (or set `API_KEY`)
- Render: set `API_KEY` in the service env

In Swagger, use Authorize and paste the key.

## API

| Method | Path | What it does |
|--------|------|----------------|
| `POST` | `/accounts` | Open account + currencies |
| `GET` | `/accounts/{accountId}` | Get account + balances |
| `POST` | `/transactions` | IN or OUT |
| `GET` | `/accounts/{accountId}/transactions` | History |

Currencies: EUR, SEK, GBP, USD.

Amounts need at most 2 decimals (e.g. `10.50`). More decimals are rejected.

Stack: Java 21, Spring Boot, MyBatis, Postgres, RabbitMQ, Gradle, JUnit 5.

## Tests

`./gradlew test` also runs `*IT` tests (needs Docker).

```bash
./gradlew test
./gradlew jacocoTestCoverageVerification
```
