# Multi-Source Microservices Data Aggregator

A distributed microservices architecture built with **Java Spring Boot**, **Spring Cloud Gateway**, **PostgreSQL**, and **Redis**.

The system aggregates multi-source distributed data (Users from Redis, Posts from PostgreSQL, and Todos from an external REST API) and delivers unified responses in both JSON and XML formats via an API Gateway.

---

## Architecture Overview

```text
                  +--------------------------------+
                  |  Client (Browser / REST / XML) |
                  +---------------+----------------+
                                  |
                                  v
                  +--------------------------------+
                  |    API Gateway (:8080)         |
                  +---------------+----------------+
                                  |
         +------------------------+------------------------+
         |                                                 |
         v                                                 v
+-------------------------------+             +-------------------------------+
|  API 1 - Spring Boot (:8081)  |             |  API 2 - Spring Boot (:8082)  |
|  Output: Combined JSON        |             |  Output: XML Feed             |
+---+---------------+-------+---+             +---------------+---------------+
    |               |       |                                 |
    v               v       v                                 v
+---------+   +---------+ +------------------------+   +---------+
|  Redis  |   | Postgres| | API 3 (External Todos) |   | Postgres|
| (Users) |   | (Posts) | | jsonplaceholder.typicode|   | (Posts) |
+---------+   +---------+ +------------------------+   +---------+
```

---

## Services Specification

| Service | Technology | Port (Host / Container) | Description |
| :--- | :--- | :--- | :--- |
| `api-gateway` | Spring Cloud Gateway | `8080:8080` | Unified routing gateway & entry point |
| `api-1-json` | Spring Boot 3 / WebClient | `8081:8081` | Combines User + Posts + Todos into JSON |
| `api-2-xml` | Spring Boot 3 / Jackson XML | `8082:8082` | Formats Posts into standardized XML |
| `postgres` | PostgreSQL 16 Alpine | `5433:5432` | Relational storage for Posts (auto-seeded) |
| `redis` | Redis 7 Alpine | `6380:6379` | In-memory key-value cache for Users |

> Note on Port Mapping:
> Host ports for PostgreSQL (5433) and Redis (6380) are mapped to non-default host ports to avoid port collision with any native PostgreSQL (5432) or Redis (6379) running on the host machine. Inside the Docker network, internal communication uses standard default ports (5432 and 6379).

---

## Quick Start

Run the entire cluster with a single command:

```bash
docker compose up -d --build
```

All dummy data (100 posts and 10 users) will be automatically loaded upon startup.
