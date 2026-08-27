# ShiftSync — Scheduling & Shift Management Service

**Student Name:** Chamith Bhanuka Widanapathirana  
**Student ID / Number:** 241711051  
**Slack Handle:** Chamith Bhanuka  
**GCP Project ID:** project-a58ee7a4-4913-4af2-a6d  
**Course:** ITS 2130 — Enterprise Cloud Architecture  

---

## Description

Core workforce scheduling and shift management microservice for the ShiftSync platform. Backed by Cloud SQL PostgreSQL, it manages business locations, employee assignments, roster scheduling, and peer-to-peer shift swap negotiation workflows with automated conflict validation and real-time notification dispatching.

---

## Key Features

- **Roster & Shift Scheduling**: Shift creation, publishing, employee assignment, and open-shift claiming.
- **Smart Shift Swaps**: Peer-to-peer shift swap requests with automated conflict checking, coworker agreement, and manager final approval/rejection workflows.
- **Inter-Service Event Dispatching**: Dispatches synchronous and asynchronous swap events (`SWAP_CREATED`, `SWAP_RESPONSE`, `SWAP_APPROVED`, `SWAP_REJECTED`) to Notification Service.
- **Transactional Consistency**: Backed by Cloud SQL PostgreSQL with JPA/Hibernate transactional guarantees.

---

## Technology Stack

- Java 25
- Spring Boot 3.x
- Spring Data JPA / Hibernate
- Cloud SQL PostgreSQL
- Spring Cloud Netflix Eureka Client
- Spring Cloud Config Client
- Maven
