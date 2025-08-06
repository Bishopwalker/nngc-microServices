version: '3.8'

services:
  service-registry:
    build: ./service-registry
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 40s

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      service-registry:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka

  customer-service:
    build: ./customer-service
    ports:
      - "8082:8082"
    depends_on:
      service-registry:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka

  token-service:
    build: ./token-service
    ports:
      - "8083:8083"
    depends_on:
      service-registry:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka

  stripe-service:
    build: ./stripe-service
    ports:
      - "8084:8084"
    depends_on:
      service-registry:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka

  email-service:
    build: ./email-service
    ports:
      - "8085:8085"
    depends_on:
      service-registry:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka

  google-service:
    build: ./google
    ports:
      - "8086:8086"
    depends_on:
      service-registry:
        condition: service_healthy
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka
