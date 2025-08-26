FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY email-service/target/*.jar email-service.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "email-service.jar"]