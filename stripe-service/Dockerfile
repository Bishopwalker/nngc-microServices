FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar stripe-service.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "stripe-service.jar"]