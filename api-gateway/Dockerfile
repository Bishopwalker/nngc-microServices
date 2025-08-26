FROM eclipse-temurin:23-jdk-alpine
LABEL authors="priest"

RUN apk add --no-cache curl

WORKDIR /app

COPY api-gateway/target/*.jar api-gateway.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]