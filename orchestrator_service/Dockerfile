FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

COPY .git /workspace/.git

COPY actuator_git_info_starter/pom.xml /workspace/actuator_git_info_starter/pom.xml
COPY actuator_git_info_starter/src /workspace/actuator_git_info_starter/src

WORKDIR /workspace/actuator_git_info_starter
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

COPY dto/pom.xml /workspace/dto/pom.xml
COPY dto/src /workspace/dto/src

WORKDIR /workspace/dto
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

COPY mask_log_starter/pom.xml /workspace/mask_log_starter/pom.xml
COPY mask_log_starter/src /workspace/mask_log_starter/src

WORKDIR /workspace/mask_log_starter
RUN mvn -B clean install -DskipTests -Dcheckstyle.skip

COPY orchestrator_service/pom.xml /workspace/orchestrator_service/pom.xml
COPY orchestrator_service/src /workspace/orchestrator_service/src

WORKDIR /workspace/orchestrator_service
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]