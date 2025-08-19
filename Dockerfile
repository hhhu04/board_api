FROM openjdk:17-jdk-slim AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar
CMD ["java", "-jar", "build/libs/board-api.jar"]