FROM openjdk:21-jdk-slim
LABEL authors="priest"
WORKDIR /app

COPY api-gateway/target/*.jar api-gateway.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]