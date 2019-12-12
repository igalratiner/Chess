FROM gradle:4.7.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/game-master
RUN gradle build

FROM openjdk:8-jre-slim

COPY --from=build /home/gradle/src/game-master/build/libs/*.jar /app/game-master.jar
ENTRYPOINT java -jar /app/game-master.jar