FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY token-service/target/*.jar token-service.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "token-service.jar"]