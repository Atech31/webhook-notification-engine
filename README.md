#Fault-Tolerant Webhook Delivery & Notification Engine

An enterprise-grade, asynchronous webhook delivery and event notification engine built with **Java 17**, **Spring Boot**, **Spring WebFlux**, and **PostgreSQL**. Designed to handle outbound webhook dispatches reliably with cryptographic signature verification and execution logging.

---

##  Architecture & Features

1. **Subscription Management:** Register subscriber endpoints mapped to specific event types (e.g., `order.created`).
2. **Cryptographic Signatures (HMAC-SHA256):** Automatically signs each outgoing payload using a shared secret key (`X-Webhook-Signature` header) for security verification.
3. **Asynchronous Execution:** Offloads HTTP dispatch network calls to a dedicated thread pool using Spring `@Async` to prevent blocking client threads.
4. **Delivery Audit Logging:** Persists every delivery attempt, tracking HTTP status codes, response messages, and attempt counts in **PostgreSQL**.

---

##  Tech Stack

* **Core Language:** Java 17
* **Framework:** Spring Boot 3.2.5
* **Reactive Client:** Spring WebFlux (`WebClient`)
* **Persistence:** Spring Data JPA & PostgreSQL
* **Containerization:** Docker & Docker Compose

---

## Getting Started Locally

### **1. Start PostgreSQL via Docker Compose**
```bash
docker compose up -d
