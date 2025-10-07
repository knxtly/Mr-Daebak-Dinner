# syntax=docker/dockerfile:1
# Build stage
FROM gradle:jdk17-jammy AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle build -x test --no-daemon

# Run stage
FROM eclipse-temurin:17-jre-jammy
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080