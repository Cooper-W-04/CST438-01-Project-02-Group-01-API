# Multi-stage Dockerfile for Spring Boot Kotlin application
# Stage 1: Build the JAR
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy Gradle wrapper and configuration files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Copy source code
COPY src src

# Build the executable Spring Boot fat JAR and copy the fat JAR (excluding plain jar) to /app/app.jar
RUN ./gradlew bootJar --no-daemon && find build/libs/ -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built fat JAR from the build stage
COPY --from=build /app/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
