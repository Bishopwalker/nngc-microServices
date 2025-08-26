FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar google-service.jar

EXPOSE 8087
ENTRYPOINT ["java", "-jar", "google-service.jar"]