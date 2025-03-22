# Stage 1: Build
FROM maven:3.6.3-jdk-11-slim AS build

# Сначала копируем .git вручную
COPY .git /workspace/.git
WORKDIR /workspace

WORKDIR /workspace/actuator_git_info_starter
COPY /actuator_git_info_starter/pom.xml /workspace/actuator_git_info_starter
COPY /actuator_git_info_starter/src /workspace/actuator_git_info_starter/src
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace/dto
COPY /dto/pom.xml /workspace/dto
COPY /dto/src /workspace/dto/src
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace/mask_log_starter
COPY /mask_log_starter/pom.xml /workspace/mask_log_starter
COPY /mask_log_starter/src /workspace/mask_log_starter/src
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

WORKDIR /workspace/orchestrator
COPY /orchestrator_service/pom.xml /workspace/orchestrator
COPY /orchestrator_service/src /workspace/orchestrator/src
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

# Stage 2: Runtime
FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]