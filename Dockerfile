FROM openjdk:17-jdk-slim
WORKDIR /app

ARG DB_HOST
ARG DB_PORT
ARG DB_NAME
ARG DB_USER
ARG DB_PASSWORD
ARG JWT_SECRET

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