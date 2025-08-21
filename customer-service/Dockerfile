FROM openjdk:21-jdk-slim

WORKDIR /app

COPY customer-service/target/*.jar customer-service.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "customer-service.jar"]