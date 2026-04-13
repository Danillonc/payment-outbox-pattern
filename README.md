# Payment Outbox Pattern Example

This project demonstrates the implementation of the Outbox Pattern using Spring Boot, PostgreSQL, Apache Kafka, and Debezium. The Outbox Pattern is a robust solution for ensuring atomicity between local database transactions and publishing messages to a message broker, crucial for building reliable distributed systems and microservices.

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Running the Application with Docker Compose](#running-the-application-with-docker-compose)
- [API Documentation](#api-documentation)
- [Database](#database)
- [Messaging](#messaging)
- [Key Features](#key-features)
- [Constitutional Principles](#constitutional-principles)

## Project Overview

This service manages payment transactions. It utilizes the Outbox Pattern to guarantee that every payment creation or status update is reliably published as an event to Kafka, even if the application crashes. This is achieved by storing outgoing messages in a dedicated "outbox" table within the same database transaction as the business data. Debezium then captures changes from this outbox table and forwards them to Kafka.

## Architecture

The project adheres to principles of Hexagonal Architecture (Ports and Adapters) and Clean Architecture, as enforced by the project's `constitution.md`.

-   **Domain Purity**: The core domain logic is isolated from infrastructure concerns, ensuring it has no direct dependencies on Spring Boot, JPA, Kafka, or other external frameworks.
-   **Ports and Adapters**: Communication with external systems (like the database or message broker) occurs through well-defined interfaces (Ports) implemented by infrastructure-specific classes (Adapters).
-   **Rich Domain Model**: Business rules and validations are encapsulated within the domain entities themselves, avoiding anemic domain models.

## Technologies Used

-   **Java 25**: The primary programming language.
-   **Spring Boot**: Framework for building the application.
-   **Spring Data JPA**: For database interaction and persistence.
-   **PostgreSQL**: Relational database for storing application data and the outbox table.
-   **Apache Kafka**: Distributed streaming platform for publishing and subscribing to events.
-   **Debezium**: Change Data Capture (CDC) platform that streams changes from PostgreSQL's outbox table to Kafka.
-   **Spring Kafka**: Spring's integration with Apache Kafka.
-   **Jakarta Validation**: For declarative input validation.
-   **Springdoc OpenAPI UI**: For automatic generation and visualization of REST API documentation (Swagger UI).
-   **Lombok**: To reduce boilerplate code.
-   **Docker & Docker Compose**: For containerization and orchestration of services.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

-   Docker and Docker Compose
-   Java Development Kit (JDK) 25
-   Maven

### Running the Application with Docker Compose

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/payment-outbox-pattern.git
    cd payment-outbox-pattern
    ```

2.  **Build the Spring Boot application:**
    ```bash
    ./mvnw clean install
    ```

3.  **Start the Docker Compose services:**
    This will bring up PostgreSQL, Kafka, Kafka UI, Debezium Connect, and the Spring Boot application.
    ```bash
    docker-compose up --build -d
    ```

4.  **Verify services are running:**
    ```bash
    docker-compose ps
    ```
    You should see all services in a healthy state.

5.  **Configure Debezium Connector:**
    Once Debezium Connect is up (it might take a minute), you need to register a connector to monitor the PostgreSQL database and the outbox table. You can do this by sending a POST request to the Debezium Connect API.

    Example `create-connector.json`:
    ```json
    {
      "name": "payment-connector",
      "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "database.hostname": "postgres",
        "database.port": "5432",
        "database.user": "user",
        "database.password": "password",
        "database.dbname": "payment_db",
        "database.server.name": "payment_server",
        "table.include.list": "public.outbox",
        "plugin.name": "pgoutput",
        "publication.autocreate.mode": "all_tables",
        "topic.prefix": "dbserver",
        "value.converter": "org.apache.kafka.connect.json.JsonConverter",
        "value.converter.schemas.enable": "false",
        "key.converter": "org.apache.kafka.connect.json.JsonConverter",
        "key.converter.schemas.enable": "false",
        "transforms": "outbox",
        "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter",
        "transforms.outbox.route.by.field": "aggregatetype",
        "transforms.outbox.route.by.value": "payment",
        "transforms.outbox.topic.replacement": "payment-events",
        "transforms.outbox.table.field.event.id": "id",
        "transforms.outbox.table.field.event.key": "aggregateid",
        "transforms.outbox.table.field.event.type": "type",
        "transforms.outbox.table.field.event.timestamp": "timestamp",
        "transforms.outbox.table.field.payload": "payload"
      }
    }
    ```
    Send this configuration to the Debezium Connect API:
    ```bash
    curl -X POST -H "Content-Type: application/json" --data @create-connector.json http://localhost:8083/connectors
    ```

## API Documentation

The REST API documentation is available via Swagger UI once the application is running:
-   [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

## Database

-   **Type**: PostgreSQL
-   **Host**: `postgres` (within Docker network)
-   **Port**: `5432`
-   **Database Name**: `payment_db`
-   **User**: `user`
-   **Password**: `password`

The database is configured with `wal_level=logical` to enable Debezium's Change Data Capture.

## Messaging

-   **Broker**: Apache Kafka
-   **Host**: `kafka` (within Docker network)
-   **Port**: `9092`
-   **Kafka UI**: Access at [http://localhost:8080](http://localhost:8080) to monitor Kafka topics and messages.

Debezium monitors the `outbox` table in PostgreSQL and publishes events to Kafka topics, specifically routing `payment` related events to `payment-events` topic.

## Key Features

-   **Outbox Pattern Implementation**: Ensures transactional consistency between database operations and message publishing.
-   **Change Data Capture (CDC)**: Leverages Debezium for reliable event streaming from the database.
-   **Event-Driven Architecture**: Facilitates decoupled microservices communication.
-   **RESTful API**: Provides endpoints for managing payment operations.
-   **Input Validation**: Strict validation of incoming request payloads using `jakarta.validation`.
-   **Containerized Environment**: Easy setup and deployment using Docker Compose.

## Constitutional Principles

This project strictly adheres to the architectural and security principles defined in `constitution.md`:

-   **Spec-Driven Development**: All APIs and event schemas are treated as the absolute source of truth.
-   **Hexagonal Architecture**: Enforces clear separation of concerns, with a pure domain layer and explicit ports and adapters.
-   **Security, Privacy, and LGPD Compliance**: Emphasizes data minimization, PII protection (e.g., no PII in logs), and secure error handling (RFC 7807).
-   **Persistence and Asynchronous Messaging**: Ensures safe database queries and decoupled, idempotent event processing.
-   **Code Quality**: Favors immutability (e.g., Java `record` types for DTOs) and constructor injection.
