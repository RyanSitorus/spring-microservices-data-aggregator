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

| Service | Directory / Module | Technology | Port (Host / Container) | Description |
| :--- | :--- | :--- | :--- | :--- |
| `api-gateway` | `api-gateway/` | Spring Cloud Gateway | `8080:8080` | Unified routing gateway & entry point |
| `data-aggregator-service` | `data-aggregator-service/` | Spring Boot 3 / WebClient | `8081:8081` | Combines User + Posts + Todos into JSON |
| `xml-feed-service` | `xml-feed-service/` | Spring Boot 3 / Jackson XML | `8082:8082` | Formats Posts into standardized XML |
| `postgres` | `docker/postgres/` | PostgreSQL 16 Alpine | `5433:5432` | Relational storage for Posts (auto-seeded) |
| `redis` | `docker/redis/` | Redis 7 Alpine | `6380:6379` | In-memory key-value cache for Users (auto-seeded) |

> Note on Port Mapping:
> Host ports for PostgreSQL (5433) and Redis (6380) are mapped to non-default host ports to avoid port collision with any native PostgreSQL (5432) or Redis (6379) running on the host machine. Inside the Docker network, internal communication uses standard default ports (5432 and 6379).

---

## API Endpoints (via API Gateway)

All client requests should be routed through the API Gateway at port `8080`:

| Method | Gateway Endpoint | Target Service | Output Format | Description |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/aggregate` | `data-aggregator-service:8081` | `application/json` | Returns all users with aggregated todos and posts |
| `GET` | `/api/v1/aggregate/{userId}` | `data-aggregator-service:8081` | `application/json` | Returns single user with aggregated todos and posts |
| `GET` | `/api/v1/posts/xml` | `xml-feed-service:8082` | `application/xml` | Returns all posts formatted in XML |
| `GET` | `/api/v1/posts/xml?userId={id}` | `xml-feed-service:8082` | `application/xml` | Returns posts filtered by user ID in XML |

---

## Quick Start

Run the entire cluster with a single command:

```bash
docker compose up -d --build
```

All dummy data (100 posts in PostgreSQL and 10 users in Redis) will be automatically loaded upon startup.

### Verification Commands

1. **Automated Verification Script**:
   ```bash
   chmod +x test-endpoints.sh
   ./test-endpoints.sh
   ```

2. **Using HTTP Client File**:
   Open `requests.http` in VS Code (with REST Client extension) or IntelliJ IDEA and execute the preconfigured requests.

3. **Manual cURL Commands**:
   - Verify JSON Aggregator (via Gateway):
     ```bash
     curl -s http://localhost:8080/api/v1/aggregate/1
     ```
   - Verify XML Feed (via Gateway):
     ```bash
     curl -s "http://localhost:8080/api/v1/posts/xml?userId=1"
     ```
   - Verify Gateway Health:
     ```bash
     curl -s http://localhost:8080/actuator/health
     ```

---

## Sample Response Formats

### JSON Aggregator Output (`/api/v1/aggregate/1`)

```json
{
  "id": 1,
  "name": "Leanne Graham",
  "username": "Bret",
  "email": "Sincere@april.biz",
  "address": {
    "street": "Kulas Light",
    "suite": "Apt. 556",
    "city": "Gwenborough",
    "zipcode": "92998-3874",
    "geo": {
      "lat": "-37.3159",
      "lng": "81.1496"
    }
  },
  "phone": "1-770-736-8031 x56442",
  "website": "hildegard.org",
  "company": {
    "name": "Romaguera-Crona",
    "catchPhrase": "Multi-layered client-server neural-net",
    "bs": "harness real-time e-markets"
  },
  "todos": [
    {
      "id": 1,
      "title": "delectus aut autem",
      "completed": false
    }
  ],
  "posts": [
    {
      "id": 1,
      "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
      "body": "quia et suscipit..."
    }
  ]
}
```

### XML Feed Output (`/api/v1/posts/xml?userId=1`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
  <row>
    <userId>1</userId>
    <id>1</id>
    <title>sunt aut facere repellat provident occaecati excepturi optio reprehenderit</title>
    <body>quia et suscipit suscipit recusandae consequuntur expedita et cum reprehenderit molestia</body>
  </row>
  <row>
    <userId>1</userId>
    <id>2</id>
    <title>qui est esse</title>
    <body>est rerum tempore vitae...</body>
  </row>
</root>
```

---

## Running Unit Tests

To run unit tests across microservices:

```bash
# Test data-aggregator-service
cd data-aggregator-service && mvn test

# Test xml-feed-service
cd ../xml-feed-service && mvn test
```

