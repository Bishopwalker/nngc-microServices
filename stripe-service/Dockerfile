FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY stripe-service/target/*.jar stripe-service.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "stripe-service.jar"]