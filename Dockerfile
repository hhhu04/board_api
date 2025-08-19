FROM openjdk:17-jdk-slim AS builder
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar
COPY ./build/libs/*.war board-api.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/board-api.jar"]