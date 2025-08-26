FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY google-service/target/*.jar google-service.jar

EXPOSE 8087
ENTRYPOINT ["java", "-jar", "google-service.jar"]