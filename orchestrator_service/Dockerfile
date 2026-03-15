FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /build

COPY pom.xml .
COPY dto/pom.xml dto/
COPY mask_log_starter/pom.xml mask_log_starter/
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/
COPY orchestrator_service/pom.xml orchestrator_service/

RUN mvn -B -pl orchestrator_service -am dependency:go-offline

COPY dto/src dto/src
COPY mask_log_starter/src mask_log_starter/src
COPY actuator_git_info_starter/src actuator_git_info_starter/src
COPY orchestrator_service/src orchestrator_service/src

RUN mvn -B -pl orchestrator_service -am clean package -DskipTests -Dcheckstyle.skip


FROM amazoncorretto:11-alpine
WORKDIR /app

COPY --from=build /build/orchestrator_service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]