FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

COPY pom.xml .
COPY orchestrator_service/pom.xml orchestrator_service/pom.xml
COPY dto/pom.xml dto/pom.xml
COPY mask_log_starter/pom.xml mask_log_starter/pom.xml
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/pom.xml

RUN mkdir recognizer_service telegram_bot_service payment_service

RUN mvn -B -q -pl orchestrator_service -am -DskipTests dependency:go-offline

COPY . .

RUN mvn -B -pl orchestrator_service -am clean package -DskipTests

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]