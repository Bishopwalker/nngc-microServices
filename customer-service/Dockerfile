FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY customer-service/target/*.jar customer-service.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "customer-service.jar"]