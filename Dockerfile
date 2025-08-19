FROM openjdk:17-jdk-slim
WORKDIR /app

ARG DBHOST
ARG DBPORT
ARG DB
ARG USERNAME
ARG PASSWORD
ARG SECRET
ARG ISSUER

ENV DBHOST=${DBHOST}
ENV DBPORT=${DBPORT}
ENV DB=${DB}
ENV USERNAME=${USERNAME}
ENV PASSWORD=${PASSWORD}
ENV SECRET=${SECRET}
ENV ISSUER=${ISSUER}

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "build/libs/board-api.jar"]