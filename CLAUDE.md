# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview
Nebula DB is a microservices-based infrastructure management platform built with Spring Boot, Kafka, and Docker. It provides a scalable and secure system for managing infrastructure resources, user authentication, and project governance.

## Architecture
The system consists of five main services:
- **nebula-auth** (port 8002): User authentication and authorization with JWT
- **project-service** (port 8000): Project and infrastructure item management
- **docker-infra-service** (port 8001): Docker container provisioning and lifecycle management
- **eureka-server** (port 8761): Service discovery and load balancing
- **api-gateway** (port 8888): Unified entry point with authentication, routing, and rate limiting

Services communicate via Apache Kafka for event-driven architecture and use Netflix Eureka for service discovery. The API Gateway acts as the entry point for all client requests.

## Project Structure
```
.
├── cloud/                  # Infrastructure services
│   ├── api-gateway/       # Spring Cloud Gateway service
│   └── eureka-server/     # Netflix Eureka service discovery
├── commonlib/             # Shared library used by all services
├── infra/                 # Docker Compose configuration
├── service-infra/         # Core microservices
│   ├── nebula-auth/       # Authentication service
│   ├── project-service/   # Project management service
│   └── docker-infra-service/ # Docker infrastructure service
├── logs/                  # Log directory
�└── .env.example           # Environment variables template
```

## Key Technologies
- **Backend**: Spring Boot 3.3 (Java 26)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: JSON Web Tokens (JWT) with Spring Security
- **Event Streaming**: Apache Kafka 3.x
- **Database**: MySQL 8.x
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## Development Commands

### Building Services
```bash
# Build all services (skip tests for faster build)
./mvnw clean install -DskipTests

# Build with tests
./mvnw clean install
```

### Running the Application
#### Using Docker Compose (Recommended)
```bash
cd infra
docker-compose up -d
# Wait for services to be healthy, then access:
# API Gateway: http://localhost:8888
# Eureka Dashboard: http://localhost:8761
```

#### Running Individually (for Development)
```bash
# Start Eureka Server
cd cloud/eureka-server
../mvnw spring-boot:run

# Start Auth Service
cd service-infra/nebula-auth
../../mvnw spring-boot:run

# Start Project Service
cd service-infra/project-service
../../mvnw spring-boot:run

# Start Docker Infra Service
cd service-infra/docker-infra-service
../../mvnw spring-boot:run

# Start API Gateway
cd cloud/api-gateway
../../mvnw spring-boot:run
```

### Testing
```bash
# Run unit tests for all services
./mvnw test

# Run tests for a specific service
cd service-infra/nebula-auth
../../mvnw test
```

### Environment Setup
1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```
2. Edit `.env` to set your configuration:
   ```env
   MYSQL_DATABASE=nebuladb
   MYSQL_USERNAME=nebula_user
   MYSQL_PASSWORD=secure_password
   JWT_SECRET_KEY=your_strong_secret_key_here_min_32_chars
   ```

## Service Ports
- nebula-auth: 8002
- project-service: 8000
- docker-infra-service: 8001
- eureka-server: 8761
- api-gateway: 8888

## API Documentation
Once services are running, access API documentation at:
- API Gateway: `http://localhost:8888/swagger-ui.html` (if enabled)
- Auth Service: `http://localhost:8002/swagger-ui.html`
- Project Service: `http://localhost:8000/swagger-ui.html`
- Docker Infra Service: `http://localhost:8001/swagger-ui.html`

> Note: Swagger UI endpoints may need to be enabled in each service's `application.yaml` for development.

## Common Development Tasks
- Adding new endpoints: Modify the relevant controller in the service
- Adding new entities: Create JPA entities in the service's `entity` package
- Adding new Kafka events: Use the publishers in the service's `kafka/publisher` package
- Configuration: Update `application.yaml` in `src/main/resources` of each service