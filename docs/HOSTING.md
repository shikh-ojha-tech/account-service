# Hosted demo

## Render + CloudAMQP

1. Create a free RabbitMQ on https://www.cloudamqp.com/
2. Deploy Blueprint: https://dashboard.render.com/blueprints/new?repo=https://github.com/shikh-ojha-tech/account-service
3. On the web service, set:
   - SPRING_RABBITMQ_HOST
   - SPRING_RABBITMQ_PORT
   - SPRING_RABBITMQ_USERNAME
   - SPRING_RABBITMQ_PASSWORD
   - API_KEY
4. Open the service URL (or `/swagger-ui.html`) and Authorize with that API key

DB user/password come from the Render Postgres binding.

If the database was already created on Postgres 18, keep it. New blueprints pin Postgres 16. The app uses Flyway 11 which supports both.

Free Render apps sleep when idle; the first request after that can be slow.

## Codespaces

https://codespaces.new/shikh-ojha-tech/account-service?quickstart=1

Open port 8080, then `/swagger-ui.html`.

## Docker (local)

```bash
docker compose up --build
```
