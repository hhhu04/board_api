FROM openjdk:17-jdk-slim AS builder
CMD ["ls -all"]
COPY ./build/libs/*.war board-api.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/board-api.jar"]