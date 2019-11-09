FROM gradle:4.7.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/Engine
RUN gradle build

FROM openjdk:8-jre-slim

COPY --from=build /home/gradle/src/Engine/build/libs/*.jar /app/engine.jar
ENTRYPOINT java -jar /app/engine.jar