FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

ENV GRADLE_OPTS="-Xmx512m -Dorg.gradle.daemon=false -Dorg.gradle.parallel=false -Dorg.gradle.workers.max=1"
ENV JAVA_TOOL_OPTIONS="-Xmx512m"

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties ./

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

COPY src src

RUN ./gradlew bootJar -x test --no-daemon --no-parallel

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ENV JAVA_TOOL_OPTIONS="-Xmx384m"
RUN apk add --no-cache curl
COPY --from=build /workspace/build/libs/*.jar app.jar
COPY docker/entrypoint.sh /app/entrypoint.sh
RUN sed -i 's/\r$//' /app/entrypoint.sh && chmod +x /app/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
