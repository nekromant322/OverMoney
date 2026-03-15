# syntax=docker/dockerfile:1.6

FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

COPY pom.xml .
COPY dto/pom.xml dto/
COPY orchestrator_service/pom.xml orchestrator_service/
COPY mask_log_starter/pom.xml mask_log_starter/
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl orchestrator_service -am clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine

WORKDIR /app

EXPOSE 8081
COPY --from=build /workspace/orchestrator/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]