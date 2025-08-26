FROM openjdk:21-jdk-slim

WORKDIR /app

COPY RegistrationService/target/*.jar registration-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "registration-service.jar"]