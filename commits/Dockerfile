FROM gradle:4.7.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/commits
RUN gradle build

FROM openjdk:8-jre-slim

COPY --from=build /home/gradle/src/commits/build/libs/*.jar /app/commits.jar
ENTRYPOINT java -jar /app/commits.jar shared-editor-db:5113