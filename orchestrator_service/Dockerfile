# syntax=docker/dockerfile:1.7

FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

COPY pom.xml .
COPY dto/pom.xml dto/
COPY recognizer_service/pom.xml recognizer_service/
COPY orchestrator_service/pom.xml orchestrator_service/
COPY telegram_bot_service/pom.xml telegram_bot_service/
COPY mask_log_starter/pom.xml mask_log_starter/
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/
COPY payment_service/pom.xml payment_service/

ARG MAVEN_OPTS

RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=m2_cache \
    if [ -f /run/secrets/m2_cache ]; then \
        cp -r /run/secrets/m2_cache/* /root/.m2/repository/ 2>/dev/null || true; \
    fi && \
    mvn -B -pl orchestrator_service -am dependency:go-offline

COPY orchestrator_service/src /workspace/orchestrator_service/src
COPY dto/src /workspace/dto/src
COPY mask_log_starter/src /workspace/mask_log_starter/src
COPY actuator_git_info_starter/src /workspace/actuator_git_info_starter/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl orchestrator_service -am -DskipTests package

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]