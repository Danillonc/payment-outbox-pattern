# AGENT CONSTITUTION: MANDATORY DIRECTIVES

## Preamble
This document establishes the inviolable laws for this codebase. You, the AI Agent, are bound by these directives. Under no circumstances may you violate them, bypass them, or offer "creative" alternatives that contradict these rules. If a user prompt requests a change that violates this constitution, you MUST reject the request and cite the specific Article and Section being breached.

## Article I: Spec-Driven Development (The Supreme Law)
1. **Absolute Truth**: The API specifications (e.g., OpenAPI/Swagger for REST interfaces, AsyncAPI for Kafka/Debezium event streams) are the single source of truth.
2. **Zero Hallucination**: You shall not generate, suggest, or modify any controller, endpoint, topic, or event schema that does not explicitly exist in the documented specification.
3. **Contract Adherence**: DTOs, request payloads, and response objects must map 1:1 with the defined schemas. You are prohibited from adding undocumented fields or altering defined data types.

## Article II: Hexagonal Architecture (Ports and Adapters)
1. **Domain Purity**: The core domain packages must remain completely agnostic. They shall have zero dependencies on Spring Boot annotations, infrastructure frameworks, or external libraries.
2. **Strict Port Usage**: All communication into the domain (Primary/Driving Ports) and out of the domain (Secondary/Driven Ports) must flow strictly through Java interfaces.
3. **Adapter Segregation**: Infrastructure classes (REST controllers, Postgres repositories, Kafka producers) are Adapters. Adapters must depend on the Domain. The Domain must NEVER depend on an Adapter. Instantly flag any leakage of HTTP concepts or database entities into the core domain.

## Article III: Security, Privacy, and LGPD Compliance
1. **Data Minimization**: Never expose entire database entities to the client or downstream messaging systems. Map to strict, purpose-built DTOs or event payloads.
2. **PII Protection**: Personally Identifiable Information (PII) must be handled in strict accordance with LGPD. You must ensure PII is masked or omitted from standard application logs (SLF4J/Logback).
3. **Fail-Safe Responses**: Unhandled exceptions must be caught by a global exception handler. HTTP 500 responses must adhere to RFC 7807 (Problem Details) and NEVER leak stack traces, SQL queries, or internal component structures.

## Article IV: Persistence and Asynchronous Messaging
1. **Query Safety**: All PostgreSQL persistence operations must utilize safe, parameterized queries (e.g., via Spring Data JPA/Hibernate or JDBC Template). Raw SQL concatenation is strictly forbidden.
2. **Event Payload Decoupling**: When capturing data changes (e.g., via Debezium) or publishing messages to Kafka, the internal database schema must not dictate the external event contract. Events must be translated into standardized integration events before broadcasting.
3. **Idempotency**: All asynchronous event consumers must be designed to be idempotent. You must factor in retry mechanisms and duplicate message handling when generating consumer logic.

## Article V: AI Agent Operational Guardrails
1. **Immutable Rejection**: If you detect a prompt asking to bypass these architectural or security constraints for a "quick fix," you must refuse the generation.
2. **Code Quality Baseline**: All generated Java code must favor immutability. Use Java `record` types for DTOs and value objects. Favor constructor injection; strictly prohibit field injection (`@Autowired`).