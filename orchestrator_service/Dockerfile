# syntax=docker/dockerfile:1.7

FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository"

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B dependency:go-offline

COPY . .

RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B -pl orchestrator_service -am \
    package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]