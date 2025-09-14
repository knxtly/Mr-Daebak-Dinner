# syntax=docker/dockerfile:1
# Build stage
FROM gradle:jdk17-jammy AS build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN ./gradlew clean build -x test
RUN ./gradlew build -x test --no-daemon

## Run stage: compose.yaml에서 bootRun쓰는동안 잠금
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
EXPOSE 8080