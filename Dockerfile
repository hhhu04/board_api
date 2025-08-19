FROM openjdk:17-jdk-slim
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/board-api.jar"]