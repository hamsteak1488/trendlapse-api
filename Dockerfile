FROM eclipse-temurin:17-jre-alpine-3.20
LABEL authors="hamsteak"

WORKDIR /app

ARG OTEL_AGENT_VERSION=2.18.1
RUN mkdir -p /otel
COPY ./opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

COPY build/libs/trendlapse-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
