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

```
┌─────────────────┐
│   Client Apps   │
└────────┬────────┘
         │ REST API / SSE
         ↓
┌─────────────────────────────────┐
│   Notification Service          │
│  ┌──────────────────────────┐  │
│  │  Controllers             │  │
│  │  - REST API              │  │
│  │  - SSE Endpoints         │  │
│  └──────────┬───────────────┘  │
│             ↓                   │
│  ┌──────────────────────────┐  │
│  │  Service Layer           │  │
│  │  - Business Logic        │  │
│  │  - Notification Handler  │  │
│  └──────────┬───────────────┘  │
│             ↓                   │
│  ┌──────────────────────────┐  │
│  │  Repository Layer        │  │
│  │  - JPA/QueryDSL          │  │
│  └──────────┬───────────────┘  │
└─────────────┼───────────────────┘
              ↓
    ┌─────────────────┐
    │   PostgreSQL    │
    └─────────────────┘
              
┌─────────────────┐
│  Apache Kafka   │ ←── Event Consumers/Producers
└─────────────────┘
```

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

