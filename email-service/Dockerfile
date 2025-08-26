FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar email-service.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "email-service.jar"]