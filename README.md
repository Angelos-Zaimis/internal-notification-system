# 🔔 Notification Service

A notification microservice implementing real-time delivery via Server-Sent Events and event-driven architecture using Apache Kafka. Built with Spring Boot for scalability and performance.

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-Enabled-black.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Running with Docker](#running-with-docker)
  - [Running Locally](#running-locally)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

This service handles notification management and distribution in distributed environments. It combines event streaming through Kafka with real-time browser delivery via SSE, ensuring reliable and immediate notification delivery.

**Core Capabilities:**
- Browser push notifications using Server-Sent Events
- Async event processing with Kafka messaging
- OAuth2-secured REST endpoints
- PostgreSQL for persistent notification storage
- OpenAPI/Swagger documentation
- Built-in health monitoring and metrics collection

## ✨ Features

### Core Features
- ✅ **Live Updates** - SSE-based real-time delivery
- ✅ **Event Streaming** - Kafka for distributed messaging
- ✅ **REST Interface** - Full notification lifecycle management
- ✅ **Status Tracking** - Read/unread state management
- ✅ **Paginated Queries** - Optimized data retrieval
- ✅ **Secured Access** - OAuth2 with role permissions

### Technical Features
- ✅ **Schema Versioning** - Flyway migrations
- ✅ **Type-Safe Queries** - QueryDSL integration
- ✅ **Object Mapping** - MapStruct for DTOs
- ✅ **Input Validation** - Jakarta Bean Validation
- ✅ **Error Management** - Centralized exception handlers
- ✅ **Metrics & Health** - Actuator with Prometheus export
- ✅ **Container Support** - Multi-stage Docker builds

## 🏗️ Architecture

### System Overview

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           NOTIFICATION SERVICE                                │
└──────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐                                    ┌──────────────────────┐
│ External System │───❶ Publish to Kafka ────────────▶│  Kafka Topic:        │
│  (Any Service)  │   (InternalNotificationDTO)        │ TOPIC_PUSH_          │
└─────────────────┘                                    │ NOTIFICATION         │
                                                       └──────┬───────────────┘
                                                              │
                                                              │ Consumes
                                                              ↓
                                                   ┌──────────────────────────┐
                                                   │ NotificationConsumer     │
                                                   │ Service                  │
                                                   └──────┬───────────────────┘
                                                          │
                                                          │ ❷ Process
                                                          ↓
                                                   ┌──────────────────────────┐
                                                   │ TopicNotification        │
                                                   │ Service                  │
                                                   │                          │
                                                   │ • Saves to DB            │
                                                   │ • Maps to DeliveryDTO    │
                                                   │ • Publishes internal     │
                                                   └──┬─────────────────┬─────┘
                                                      │                 │
                             ❸ Persist to DB         │                 │ ❹ Publish
                                                      │                 │
                                                      ↓                 ↓
                                            ┌────────────────┐  ┌──────────────────┐
                                            │   PostgreSQL   │  │  Kafka Topic:    │
                                            │                │  │ INTERNAL_TOPIC_  │
                                            │  • Notification│  │ PUSH_NOTIFICATION│
                                            │  • Category    │  └────┬─────────────┘
                                            │  • Level       │       │
                                            └────────────────┘       │ Consumes
                                                      ▲              │
                                                      │              ↓
                                            ❼ Query   │    ┌──────────────────────┐
                                              Update  │    │ Notification         │
                                                      │    │ Dispatcher           │
                                                      │    │                      │
                                                      │    │ • Dispatches via SSE │
                                         ┌────────────┴──┐ │ • Updates DB status  │
                                         │ Notification  │ └──────┬───────────────┘
                                         │ QueryService  │        │
                                         └───────────────┘        │ ❺ Push notification
                                                ▲                 │
                                                │                 ↓
                                         ❻ REST │       ┌──────────────────────┐
                                           API  │       │   SsePushService     │
                                                │       │                      │
┌──────────────────┐                            │       │ • Manages emitters   │
│   Web Client     │◀───────────────────────────┘       │ • Real-time push     │
│                  │         JSON Response              │ • Keep-alive pings   │
│  • Subscribe SSE │                                    └──────┬───────────────┘
│  • Query API     │                                           │
│  • Mark as read  │◀──────────────────────────────────────────┘
└──────────────────┘              ❽ SSE Stream
                                (Server-Sent Events)
```

### Flow Breakdown

**❶ External Notification Creation**
- Any external service publishes notification to Kafka topic `TOPIC_PUSH_NOTIFICATION`
- Uses `InternalNotificationDTO` format

**❷ Notification Processing**
- `NotificationConsumerService` consumes from Kafka
- Delegates to `TopicNotificationService` for business logic

**❸ Persistence**
- Notification saved to PostgreSQL database
- Initial state: `delivered=false`, `read=false`

**❹ Internal Dispatch**
- Converts to `NotificationDeliveryDTO`
- Publishes to internal topic `INTERNAL_TOPIC_PUSH_NOTIFICATION`
- This decouples persistence from delivery

**❺ Real-Time Delivery**
- `NotificationDispatcher` consumes internal topic
- Sends via `SsePushService` to connected clients
- Updates database: `delivered=true`, `sendDate=now()`

**❻ REST API Operations**
- `GET /notifications` - Paginated list
- `PUT /notifications/{id}/read` - Mark as read
- `DELETE /notifications/{id}` - Delete notification

**❼ Database Queries**
- `NotificationQueryService` handles all read operations
- Uses authenticated user context
- Supports pagination and filtering

**❽ SSE Subscription**
- Clients connect to `GET /sse/subscribe/{userId}`
- Maintains persistent connection
- Receives notifications instantly
- 15-second keep-alive pings

### Key Components

#### 1. DTOs (Data Transfer Objects)

**InternalNotificationDTO** - Incoming notification from external services
```java
{
  "userId": "uuid",
  "senderId": "uuid", 
  "title": "string",
  "body": "string",
  "resourceId": "uuid",
  "category": "TOPIC|EVENT|SYSTEM",
  "level": "INFO|WARNING|ERROR",
  "actionUrl": "string",
  "translationArgs": {}
}
```

**NotificationDeliveryDTO** - For internal Kafka and SSE delivery
```java
{
  "id": "uuid",
  "userId": "uuid",
  "senderId": "uuid",
  "title": "string",
  "body": "string",
  "resourceId": "uuid",
  "category": "TOPIC",
  "level": "INFO",
  "delivered": true,
  "read": false,
  "sendDate": "2024-01-01T10:00:00",
  "createdAt": "2024-01-01T10:00:00",
  "actionUrl": "string",
  "translationArgs": {}
}
```

**NotificationTxDTO** - REST API response format
- Similar to DeliveryDTO, used for client responses

#### 2. Services

**TopicNotificationService**
- Implements `NotificationHandler` interface
- Processes incoming notifications from external Kafka topic
- Persists to database and forwards to internal topic

**NotificationDispatcher**
- Consumes internal Kafka topic
- Dispatches notifications via SSE
- Updates delivery status in database

**NotificationQueryService**
- Handles all REST API read operations
- Implements user authentication checks
- Provides paginated queries

**SsePushService**
- Manages SSE connections per user
- Sends real-time notifications
- Implements keep-alive mechanism

#### 3. Kafka Topics

| Topic Name | Purpose | Consumer | Producer |
|------------|---------|----------|----------|
| `topic-push-notifications` | External notifications | NotificationConsumerService | External Services |
| `internal-topic-push-notifications` | Internal dispatch queue | NotificationDispatcher | TopicNotificationService |

#### 4. Database Schema

**notifications** table
```sql
- id (UUID, PK)
- user_id (UUID, NOT NULL)
- sender_id (UUID)
- title (VARCHAR)
- body (TEXT)
- resource_id (UUID)
- category (VARCHAR, NOT NULL)
- level (VARCHAR, NOT NULL)
- delivered (BOOLEAN, NOT NULL)
- read (BOOLEAN, NOT NULL)
- retry_count (INTEGER)
- action_url (VARCHAR)
- send_date (TIMESTAMP)
- created_at (TIMESTAMP)
- translation_args (JSONB)
```

### Usage Examples

#### Sending a Notification (External Service)

Publish to Kafka topic `topic-push-notifications`:

```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "senderId": "123e4567-e89b-12d3-a456-426614174001",
  "title": "New Assignment Available",
  "body": "A new assignment has been posted in Mathematics",
  "resourceId": "123e4567-e89b-12d3-a456-426614174002",
  "category": "TOPIC",
  "level": "INFO",
  "actionUrl": "/courses/math/assignments/123"
}
```

#### Subscribing to Real-Time Notifications (Client)

```javascript
const userId = '123e4567-e89b-12d3-a456-426614174000';
const eventSource = new EventSource(
  `http://localhost:8080/sse/subscribe/${userId}`,
  { withCredentials: true }
);

eventSource.addEventListener('notification', (event) => {
  const notification = JSON.parse(event.data);
  console.log('Received:', notification);
  displayNotification(notification);
});

eventSource.addEventListener('ping', () => {
  console.log('Keep-alive ping received');
});

eventSource.onerror = (error) => {
  console.error('SSE error:', error);
};
```

#### Querying Notifications via REST API

```bash
# Get paginated notifications
curl -X GET "http://localhost:8080/notifications?page=0&size=10" \
  -H "Authorization: Bearer {token}"

# Mark notification as read
curl -X PUT "http://localhost:8080/notifications/{id}/read" \
  -H "Authorization: Bearer {token}"

# Delete notification
curl -X DELETE "http://localhost:8080/notifications/{id}" \
  -H "Authorization: Bearer {token}"
```

### Design Patterns & Best Practices

✅ **Event-Driven Architecture** - Kafka decouples producers from consumers  
✅ **Separation of Concerns** - Clear boundaries between layers  
✅ **Retry Mechanism** - Automatic retries on failure with exponential backoff  
✅ **Idempotency** - Safe to retry operations  
✅ **OAuth2 Security** - Role-based access control  
✅ **Real-Time Push** - SSE for instant delivery  
✅ **Persistent Storage** - PostgreSQL ensures no data loss  
✅ **Graceful Degradation** - Offline users receive notifications on reconnect

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Apache Kafka community
- PostgreSQL development team

## 📞 Contact

For questions or support, please open an issue on GitHub.

---

**Made with ❤️ using Spring Boot**

