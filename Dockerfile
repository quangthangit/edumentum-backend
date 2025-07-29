# ====================== BUILD STAGE ======================
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle config for layer caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies only (faster rebuild later)
RUN ./gradlew build -x test || return 0

# Copy source code and re-build
COPY src src
RUN ./gradlew bootJar -x test

# ====================== RUNTIME STAGE ======================
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the generated JAR
COPY --from=build /app/build/libs/*.jar app.jar

# Set Spring profile to production
ENV SPRING_PROFILES_ACTIVE=production

# Expose app port
EXPOSE 8080

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
