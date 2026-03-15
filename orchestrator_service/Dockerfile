# Stage 1: Build
FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

COPY pom.xml .
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/
COPY dto/pom.xml dto/
COPY mask_log_starter/pom.xml mask_log_starter/
COPY orchestrator_service/pom.xml orchestrator_service/

RUN mvn -B dependency:go-offline

COPY actuator_git_info_starter/src actuator_git_info_starter/src
COPY dto/src dto/src
COPY mask_log_starter/src mask_log_starter/src
COPY orchestrator_service/src orchestrator_service/src

RUN mvn -B clean package -DskipTests -Dcheckstyle.skip

# Stage 2: Runtime
FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]