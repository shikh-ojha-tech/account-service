# Run / review

## Docker

```bash
docker compose up --build
```

Swagger: http://localhost:8080/swagger-ui.html  
API key: `demo-api-key`

## Codespaces

1. Open the repo on GitHub
2. Code -> Codespaces -> Create codespace on main
3. Wait for compose
4. Open port 8080 -> `/swagger-ui.html`

## Render

See [HOSTING.md](HOSTING.md).

## Smoke test

1. Create account with EUR and USD
2. IN
3. OUT
4. OUT too much -> `INSUFFICIENT_FUNDS`
5. List transactions
