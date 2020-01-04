FROM gradle:4.7.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/Access
RUN gradle build

FROM openjdk:8-jre-slim

COPY --from=build /home/gradle/src/Access/build/libs/*.jar /app/access.jar
ENTRYPOINT java -jar /app/access.jar shared-editor-db:5113