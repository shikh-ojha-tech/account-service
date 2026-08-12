#!/bin/sh
set -eu

if [ -n "${DB_HOST:-}" ] && [ -n "${DB_NAME:-}" ]; then
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT:-5432}/${DB_NAME}"
fi

if [ -n "${DATABASE_URL:-}" ] && [ -z "${SPRING_DATASOURCE_URL:-}" ]; then
  export SPRING_DATASOURCE_URL="$(printf '%s' "$DATABASE_URL" \
    | sed -e 's#^postgres://#jdbc:postgresql://#' -e 's#^postgresql://#jdbc:postgresql://#')"
fi

export SERVER_PORT="${PORT:-${SERVER_PORT:-8080}}"

exec java -jar /app/app.jar
