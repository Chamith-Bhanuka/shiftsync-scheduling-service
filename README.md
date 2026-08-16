# ShiftSync — Scheduling Service

[//]: # (**Student Name:** <YOUR FULL NAME>)

[//]: # (**Student Number:** <YOUR STUDENT ID>)

[//]: # (**Slack Handle:** <YOUR SLACK HANDLE — optional>)

[//]: # (**GCP Project ID:** <YOUR GCP PROJECT ID>)

---

## Description

The Scheduling Service is one of three microservices that make up the ShiftSync platform — a shift-scheduling and shift-swap system for businesses that run on hourly workers. This service owns the core scheduling data: businesses, locations, employees, shifts, and shift-swap requests.

It is backed by **PostgreSQL** because this data is highly relational — a shift must always belong to a real location, a swap request must always reference a real shift and real employees, and these relationships must stay consistent. This service registers itself with Eureka and is reached by other parts of the system only through the API Gateway, never directly.

---

## Technology Stack

- Java 25
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Spring Cloud Config Client
- Netflix Eureka Client
- Maven

---

## Architecture Role

```
Frontend → API Gateway → Scheduling Service → PostgreSQL
                              ↑
                        Eureka (service discovery)
                              ↑
                        Config Server (settings)
```

This service does not call any other microservice directly. Cross-service actions (e.g., notifying an employee when a swap is approved) are intentionally left as a future extension point and are **not currently implemented** — see [Known Limitations](#known-limitations) below.

---

## Setup / Getting Started

### Prerequisites
- Java 25 installed
- Maven installed
- PostgreSQL running locally
- Config Server and Eureka Server already running (this service depends on both at startup)

### 1. Create the database
```bash
psql -U postgres
```
```sql
CREATE DATABASE shiftsync_scheduling;
\q
```

### 2. Confirm the Config Server has this service's settings
Make sure `config-repo/scheduling-service.yml` exists in the `config-server` project with the correct database URL, username, and password for your environment.

### 3. Start dependencies first, in this order
1. `config-server` (port 8888)
2. `eureka-server` (port 8761)

### 4. Run this service
```bash
mvn spring-boot:run
```
The service starts on **port 8081** and registers itself with Eureka as `SCHEDULING-SERVICE`.

### 5. Verify it's running
```bash
curl "http://localhost:8081/shifts?locationId=1"
```
A `200 OK` with an empty array `[]` (on a fresh database) confirms the service is up and connected to PostgreSQL correctly.

---

## API Reference

All endpoints below are called directly on port 8081 for local testing. In the full system, the frontend reaches these through the API Gateway at `http://localhost:8080/api/scheduling/...` (the `/api/scheduling` prefix is stripped by the Gateway before forwarding).

### Businesses
| Method | Path | Description |
|---|---|---|
| POST | `/businesses` | Create a business |
| GET | `/businesses` | List all businesses |

### Locations
| Method | Path | Description |
|---|---|---|
| POST | `/locations` | Create a location under a business |
| GET | `/locations?businessId=` | List locations for a business |

### Employees
| Method | Path | Description |
|---|---|---|
| POST | `/employees` | Create an employee under a location |
| GET | `/employees?locationId=` | List employees for a location |

### Shifts
| Method | Path | Description |
|---|---|---|
| POST | `/shifts` | Create a shift (assign an employee, or omit `employeeId` for an open shift) |
| GET | `/shifts?locationId=` | List shifts for a location |

### Swap Requests
| Method | Path | Description |
|---|---|---|
| POST | `/swap-requests` | Request a shift swap between employees |
| PUT | `/swap-requests/{id}/approve` | Manager approves a swap — reassigns the shift and marks it `COVERED` |
| PUT | `/swap-requests/{id}/reject` | Manager rejects a swap — no change to the shift |

### Example request bodies

**Create a shift:**
```json
POST /shifts
{
  "locationId": 1,
  "employeeId": 1,
  "startTime": "2026-08-20T09:00:00",
  "endTime": "2026-08-20T17:00:00"
}
```

**Create a swap request:**
```json
POST /swap-requests
{
  "shiftId": 1,
  "requestingEmployeeId": 1,
  "targetEmployeeId": 2
}
```

---

## Data Model

| Entity | Key Fields | Relationships |
|---|---|---|
| Business | id, name | has many Locations |
| Location | id, name, address | belongs to Business, has many Employees |
| Employee | id, name, email, role | belongs to Location |
| Shift | id, startTime, endTime, status (`SCHEDULED`/`OPEN`/`COVERED`) | belongs to Location, optionally to Employee |
| SwapRequest | id, status (`PENDING`/`APPROVED`/`REJECTED`), createdAt | belongs to Shift, requesting Employee, optional target Employee |

---

## Local Testing

A full end-to-end curl test sequence (create business → location → employees → shift → swap request → approve/reject) is documented separately and was used to verify this service before any cloud deployment work began.
