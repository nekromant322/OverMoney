# syntax=docker/dockerfile:1.4
FROM maven:3.6.3-jdk-11-slim AS build

# Сначала копируем .git вручную
COPY .git /workspace/.git
WORKDIR /workspace

WORKDIR /workspace/actuator_git_info_starter
COPY /actuator_git_info_starter/pom.xml /workspace/actuator_git_info_starter
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline \

COPY /actuator_git_info_starter/src /workspace/actuator_git_info_starter/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace/dto
COPY /dto/pom.xml /workspace/dto
COPY /dto/src /workspace/dto/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace/mask_log_starter
COPY /mask_log_starter/pom.xml /workspace/mask_log_starter
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline \
COPY /mask_log_starter/src /workspace/mask_log_starter/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace/orchestrator
COPY /orchestrator_service/pom.xml /workspace/orchestrator
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline \
COPY /orchestrator_service/src /workspace/orchestrator/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean install -DskipTests -Dcheckstyle.skip

# Stage 2: Runtime
FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]