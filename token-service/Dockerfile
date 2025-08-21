FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar token-service.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "token-service.jar"]