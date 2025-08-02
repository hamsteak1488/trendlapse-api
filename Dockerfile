FROM eclipse-temurin:17-alpine
LABEL authors="hamsteak"

WORKDIR /app

RUN cp build/libs/trendlapse-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
