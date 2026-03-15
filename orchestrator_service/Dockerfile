FROM maven:3.6.3-jdk-11-slim AS build

COPY .git /workspace/.git
WORKDIR /workspace

COPY pom.xml /workspace/pom.xml
COPY actuator_git_info_starter/pom.xml /workspace/actuator_git_info_starter/pom.xml
COPY dto/pom.xml /workspace/dto/pom.xml
COPY mask_log_starter/pom.xml /workspace/mask_log_starter/pom.xml
COPY orchestrator_service/pom.xml /workspace/orchestrator_service/pom.xml

RUN mvn -B dependency:go-offline

COPY actuator_git_info_starter /workspace/actuator_git_info_starter
COPY dto /workspace/dto
COPY mask_log_starter /workspace/mask_log_starter
COPY orchestrator_service /workspace/orchestrator_servic

RUN mvn -B -T 1C clean install -DskipTests -Dcheckstyle.skip -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]