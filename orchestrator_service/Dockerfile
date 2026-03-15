# syntax=docker/dockerfile:1.7

FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository"

COPY pom.xml .

COPY dto/pom.xml dto/pom.xml
COPY mask_log_starter/pom.xml mask_log_starter/pom.xml
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/pom.xml

RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B -e dependency:go-offline

RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B -e package -DskipTests

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]