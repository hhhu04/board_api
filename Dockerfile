FROM eclipse-temurin:17-jdk-alpine AS builder
COPY build/libs/*.war board-api.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/board-api.jar"]