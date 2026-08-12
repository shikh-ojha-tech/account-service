# Run / review

## Live demo

https://account-service-t0j6.onrender.com/swagger-ui/index.html

1. Open the link
2. Authorize with `demo-api-key`
3. Follow **Try it** in the README

## Docker (localhost)

You only need Docker Desktop installed and running.

```bash
git clone https://github.com/shikh-ojha-tech/account-service
cd account-service
docker compose up --build
```

1. Open http://localhost:8080/swagger-ui.html
2. Authorize with `demo-api-key`
3. Follow **Try it** in the README

No need to install Java, Postgres, or RabbitMQ yourself.

## Codespaces

1. Open the repo on GitHub
2. Code -> Codespaces -> Create codespace on main
3. Wait for compose
4. Open port 8080 -> `/swagger-ui.html`
5. Authorize with `demo-api-key`, then follow **Try it** in the README

## Render setup

See [HOSTING.md](HOSTING.md).
