# Design notes

## Goal

Multi-currency account API with:

- accounts in EUR / SEK / GBP / USD
- balance per currency
- IN / OUT transactions
- history
- RabbitMQ events on insert/update
- Docker Compose
- tests at 80%+ coverage

## Layout

- App: Spring Boot
- Postgres: data
- RabbitMQ: events (`account.exchange` / `account.changes`)

Packages: `api`, `service`, `persistence`, `messaging`, `domain`

## Data

- `account`: customer, country
- `balance`: amount per currency (not below zero)
- `account_transaction`: ledger; `ledger_seq` orders history newest-first
- `outbox_event`: pending RabbitMQ messages, written in the same DB transaction as the change

Schema is created by Flyway on startup.

## Flows

Create account: validate currencies, insert account + balances, write outbox rows.

Get account: load account + balances, or 404.

Create transaction: validate, lock balance (`FOR UPDATE`), apply IN/OUT, reject insufficient funds, update balance + insert ledger, write outbox rows.

Amounts allow at most 2 decimals. Extra decimals are rejected (`INVALID_AMOUNT`).

List transactions: newest first by `ledger_seq`. Create response includes balance after; list does not.

## Errors

`DomainException` with codes like `INVALID_CURRENCY`, `INSUFFICIENT_FUNDS`. Handler returns JSON + 400/404.

## Notes

- MyBatis keeps SQL (including locks) visible
- Outbox: DB commit first, then a scheduled job pushes to RabbitMQ
- API key via `X-API-Key` (simple shared key for this project)
- Local DB login is only in `application-local.yml`; docker/Render use env vars
- Swagger at `/swagger-ui.html`

## Tests

Unit tests for services/controllers. Integration tests with Testcontainers. Jacoco gate 80%.

## Throughput

Roughly 50-200 TPS for create-transaction on a laptop with Docker, depending on lock contention.

## Scaling

Stateless app behind a load balancer, shared Postgres. Outbox poller uses `FOR UPDATE SKIP LOCKED`.

## Smoke check

1. Start the stack
2. Open Swagger
3. Create account (EUR + USD)
4. IN then OUT
5. Oversized OUT -> `INSUFFICIENT_FUNDS`
6. List transactions
