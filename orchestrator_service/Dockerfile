FROM maven:3.9.9-eclipse-temurin-11 AS build

WORKDIR /workspace

COPY pom.xml .

COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/
COPY dto/pom.xml dto/
COPY mask_log_starter/pom.xml mask_log_starter/
COPY orchestrator_service/pom.xml orchestrator_service/

COPY recognizer_service/pom.xml recognizer_service/
COPY telegram_bot_service/pom.xml telegram_bot_service/
COPY payment_service/pom.xml payment_service/

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -e -DskipTests -Dcheckstyle.skip \
    -pl orchestrator_service -am dependency:go-offline

COPY actuator_git_info_starter/src actuator_git_info_starter/src
COPY dto/src dto/src
COPY mask_log_starter/src mask_log_starter/src
COPY orchestrator_service/src orchestrator_service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests -Dcheckstyle.skip \
    -pl orchestrator_service -am package


FROM amazoncorretto:11-alpine

WORKDIR /app

EXPOSE 8081

COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]