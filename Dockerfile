# Multi-stage Dockerfile for Notification Service
# Stage 1: Build the application
FROM gradle:8.5-jdk17-alpine AS build

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine

# Set metadata labels
LABEL maintainer="your-email@example.com"
LABEL description="Notification Service - Real-time notification microservice"
LABEL version="0.0.1-SNAPSHOT"

# Set environment variables
ENV LANG='en_US.UTF-8' \
    LANGUAGE='en_US:en' \
    LC_ALL='en_US.UTF-8' \
    TZ='UTC'

# Install curl for healthcheck
RUN apk add --no-cache curl tzdata

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Create application directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8281

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8281/middleware-notification/actuator/health || exit 1

# JVM configuration for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -XX:-OmitStackTraceInFastThrow \
    -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

