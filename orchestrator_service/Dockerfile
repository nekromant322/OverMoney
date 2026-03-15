FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /workspace

COPY pom.xml .
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/
COPY dto/pom.xml dto/
COPY mask_log_starter/pom.xml mask_log_starter/
COPY orchestrator_service/pom.xml orchestrator_service/

COPY actuator_git_info_starter/src actuator_git_info_starter/src
COPY dto/src dto/src
COPY mask_log_starter/src mask_log_starter/src
COPY orchestrator_service/src orchestrator_service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl orchestrator_service -am \
    clean package -DskipTests -Dcheckstyle.skip


FROM amazoncorretto:11-alpine
WORKDIR /app

COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]