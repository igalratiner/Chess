FROM gradle:4.7.0-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/texts
RUN gradle build

FROM openjdk:8-jre-slim

COPY --from=build /home/gradle/src/texts/build/libs/*.jar /app/texts.jar
ENTRYPOINT java -jar /app/texts.jar shared-editor-db:5113