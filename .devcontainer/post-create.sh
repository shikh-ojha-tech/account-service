# First Codespace create: build and start the stack.
set -euo pipefail

echo "Starting docker compose..."
docker compose up --build -d

echo "Waiting for API on :8080..."
for i in $(seq 1 60); do
  if curl -sf "http://localhost:8080/v3/api-docs" >/dev/null 2>&1; then
    echo "Ready. Open port 8080 -> /swagger-ui.html"
    exit 0
  fi
  sleep 5
done

echo "Still starting. Check: docker compose logs -f app"
docker compose ps
