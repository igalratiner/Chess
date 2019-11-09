FROM gradle:4.7.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/Server
RUN gradle build

FROM openjdk:8-jre-slim

COPY --from=build /home/gradle/src/Server/build/libs/*.jar /app/server.jar
ENTRYPOINT java -jar /app/server.jar