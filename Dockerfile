# Build stage
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar

# Runtime stage
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/build/libs/board-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]