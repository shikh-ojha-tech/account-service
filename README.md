# Account Service

Multi-currency accounts, balances, IN/OUT transactions, history, and RabbitMQ events.

Repo: https://github.com/shikh-ojha-tech/account-service

## Live demo

Swagger: https://account-service-t0j6.onrender.com/swagger-ui/index.html

1. Open the link (first load after sleep can take a minute)
2. Click **Authorize**, enter `demo-api-key`, confirm
3. Follow **Try it** below

Codespaces: https://codespaces.new/shikh-ojha-tech/account-service?quickstart=1

## Try it

### 1. Create account

`POST /accounts`

```json
{
  "customerId": "cust-1",
  "country": "EE",
  "currencies": ["EUR", "USD"]
}
```

Expect `201`. Copy `accountId` from the response.

### 2. Get account

`GET /accounts/{accountId}`

Expect `200` with both currencies at `0.00`.

### 3. Deposit

`POST /transactions`

```json
{
  "accountId": "PASTE-ACCOUNT-ID",
  "amount": 10.00,
  "currency": "EUR",
  "direction": "IN",
  "description": "Deposit"
}
```

Expect `201` and `balanceAfterTransaction` = `10.00`.

### 4. Withdraw

Same call with `"direction": "OUT"`, `"amount": 3.00`, `"description": "Spend"`.

Expect `201` and balance after = `7.00`.

### 5. Reject overspend

Same as withdraw but `"amount": 999.00`.

Expect `400` and `"code": "INSUFFICIENT_FUNDS"`.

### 6. History

`GET /accounts/{accountId}/transactions`

Expect newest first (spend, then deposit).

## Docker (localhost)

You only need Docker Desktop installed and running.

```bash
git clone https://github.com/shikh-ojha-tech/account-service
cd account-service
docker compose up --build
```

1. Open http://localhost:8080/swagger-ui.html
2. Click **Authorize**, enter `demo-api-key`, confirm
3. Follow **Try it** above

No need to install Java, Postgres, or RabbitMQ yourself.

## Auth

Header: `X-API-Key`  
Local / demo: `demo-api-key`  
Render: use `API_KEY` from the service env if you changed it

## API

| Method | Path | What it does |
|--------|------|----------------|
| `POST` | `/accounts` | Open account + currencies |
| `GET` | `/accounts/{accountId}` | Get account + balances |
| `POST` | `/transactions` | IN or OUT |
| `GET` | `/accounts/{accountId}/transactions` | History |

Currencies: EUR, SEK, GBP, USD.

Amounts: max 2 decimals (e.g. `10.50`).

Stack: Java 21, Spring Boot, MyBatis, Postgres, RabbitMQ, Gradle, JUnit 5.

## Tests

Needs Docker for integration tests.

```bash
./gradlew test
./gradlew jacocoTestCoverageVerification
```
