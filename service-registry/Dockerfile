FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY target/*.jar service-registry.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "service-registry.jar"]