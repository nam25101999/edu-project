# University Management System - Core Backend

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A production-ready Enterprise Resource Planning (ERP) backend for universities, built with a refined Modular Monolith architecture. This system manages everything from student admissions and academic registration to finance and e-learning.

---

## 🚀 Project Overview

The University Management System (UMS) backend provides a centralized, secure, and scalable API for higher education institutions. It is designed to handle complex academic workflows while maintaining high performance and strict data integrity.

### Key Highlights
- **Architecture**: Modular Monolith to balance development speed with logical separation.
- **Standards**: Enterprise-grade patterns (6-layer structure, DTO-only exposure, standardized responses).
- **Security**: Stateless JWT authentication with granular Role-Based Access Control (RBAC).
- **Quality**: 100% integration coverage for critical paths using MockMvc and H2.

---

## ✨ Features

The system is divided into **14 functional modules**:

- **Auth**: JWT-based authentication, password recovery, and session management.
- **Academic**: Management of academic years, semesters, and course sections.
- **Curriculum**: Department, Major, and Training Program structures.
- **E-learning**: Assignments, attendance tracking, and learning materials.
- **Student**: Comprehensive student profiles and lifecycle management.
- **Student Service**: Petitions, surveys, and conduct score management.
- **Grading**: Standardized grading scales and transcript generation.
- **Registration**: Course registration workflows and capacity management.
- **Finance**: Tuition fee calculations and payment tracking.
- **HR**: Employee and department management.
- **Schedule**: Timetabling and room management.
- **Examination**: Exam scheduling and proctoring layout.
- **Graduation**: Graduation eligibility checking and degree management.
- **System**: Global configuration and audit logging.

---

## 🏗️ Architecture & Design Decisions

### Modular Monolith Strategy
The project follows a **Modular Monolith** architecture. This approach was chosen to reduce the operational complexity associated with microservices while maintaining high scalability and logical separation between university domains.

### 6-Layer Structure
Each module follows a strict 6-layer architecture to ensure separation of concerns:

1. **Controller**: REST Endpoints (Handled by `ResponseEntity<BaseResponse<T>>`).
2. **DTO**: Data Transfer Objects (Input/Output validation, no direct Entity exposure).
3. **Mapper**: MapStruct interfaces handling DTO-Entity conversions.
4. **Service**: Business logic and transaction management.
5. **Repository**: Data access layer (Spring Data JPA).
6. **Entity**: JPA/Hibernate Database models.

### Key Decisions
- **Zero Entity Exposure**: The DTO pattern is strictly enforced across all modules to ensure decoupled data structures.
- **Centralized BaseResponse**: All API interactions utilize a unified response object, significantly improving frontend consistency and error handling.
- **MapStruct for Performance**: Bytecode-level mapping ensures minimal overhead during object conversion.

---

## 📂 Project Structure

```text
src/main/java/com/edu/university
├── common/                 # Shared resources (Exception Handling, Response Wrapper)
│   ├── exception/          # GlobalExceptionHandler & BusinessException
│   └── response/           # BaseResponse<T> implementation
├── config/                 # Security, JPA, and Global Bean configurations
└── modules/                # 14 Functional Modules
    ├── auth/
    ├── academic/
    ├── curriculum/         # (Each module contains 6 sub-layers)
    │   ├── controller/
    │   ├── dto/
    │   ├── mapper/
    │   ├── service/
    │   ├── repository/
    │   └── entity/
    └── ... 
```

## 🔄 Module Interaction

To maintain high cohesion and low coupling, cross-module interactions are managed via well-defined Service-to-Service interfaces:

- **Registration**: Depends on the **Academic** and **Student** modules for eligibility and capacity checks.
- **Finance**: Integrates with the **Registration** module to trigger tuition fee calculations upon enrollment.
- **E-learning**: Relies on **Registration** to sync student rosters for specific course sections.

---

## ⚡ Performance Optimization

- **Pagination**: Mandatory `Pageable` support applied to all list and search endpoints to prevent high memory consumption.
- **DTO Projection**: Optimized JPA queries using selective DTO projections to reduce database I/O.
- **Caching Strategy**: Redis-based distributed caching is planned for high-traffic metadata (Academic Years, Course Catalogs).
- **Batch Processing**: Used for large-scale operations like tuition generation and transcript calculations.

---

## 🚦 Getting Started

### Prerequisites
- JDK 21
- Maven 3.9+
- MS SQL Server (Optional for local dev, uses H2 for tests)

### Running the Application
```bash
mvn spring-boot:run
```
The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Running Tests
```bash
# Run all tests (Unit + Integration)
mvn test
```

---

## 📡 API Standards

### Standard Response Format
All API responses follow the `BaseResponse<T>` pattern for consistency:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "errors": null,
  "timestamp": "2024-04-06T11:13:38"
}
```

### Security Flow
1. **Authentication**: POST `/api/auth/login` returns a JWT token.
2. **Authorization**: Include the token in the `Authorization: Bearer <token>` header for protected routes.
3. **RBAC**: Access to endpoints is restricted based on roles (e.g., `ADMIN`, `LECTURER`, `STUDENT`).

---

## 🧪 Testing Strategy

- **Unit Tests**: JUnit 5 + Mockito. Focus on isolated service logic.
- **Integration Tests (IT)**: `BaseIntegrationTest` using `MockMvc` and H2 in-memory database.
    - Validates full Controller-to-DB flow.
    - Enforces Security context validation using `@WithMockUser`.

---

## 🛠️ Development Principles

- **Zero Entity Exposure**: Entities must never leave the Service layer. Use DTOs for all Controller interactions.
- **Standardized Error Handling**: Use `BusinessException` with predefined `ErrorCode` for all business-level failures.
- **Soft Delete**: All data-sensitive entities use `@SQLRestriction` for soft-deletion.
- **Auditing**: Automatic `createdAt`, `updatedAt`, `createdBy`, and `updatedBy` tracking via JPA Auditing.

---

## 🗺️ Roadmap
- [ ] Migrate to Spring Boot 3.3.x.
- [ ] Implement Redis-based distributed caching.
- [ ] Expand Audit Logging to a dedicated ELK stack.
- [ ] Add WebSocket support for real-time notifications.

---

*Developed by the University IT Department.*
