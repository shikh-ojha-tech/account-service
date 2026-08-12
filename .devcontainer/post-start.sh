# Codespace restart: start compose again if needed.
set -euo pipefail

if ! docker compose ps --status running --services 2>/dev/null | grep -q '^app$'; then
  echo "Starting compose..."
  docker compose up -d
fi
