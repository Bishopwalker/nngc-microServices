FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY registration-service/target/*.jar registration-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "registration-service.jar"]