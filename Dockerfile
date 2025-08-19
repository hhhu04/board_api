FROM openjdk:17-jdk-slim AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar
COPY ./build/libs/*.war board-api.jar
CMD ["java", "-jar", "build/libs/board-api.jar"]