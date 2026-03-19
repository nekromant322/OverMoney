FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

COPY pom.xml .
COPY orchestrator_service/pom.xml orchestrator_service/pom.xml
COPY dto/pom.xml dto/pom.xml
COPY mask_log_starter/pom.xml mask_log_starter/pom.xml
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/pom.xml
COPY recognizer_service/pom.xml recognizer_service/pom.xml
COPY telegram_bot_service/pom.xml telegram_bot_service/pom.xml
COPY payment_service/pom.xml payment_service/pom.xml

RUN mvn -B -q -pl recognizer_service -am -DskipTests dependency:go-offline

COPY . .

RUN mvn -B -pl recognizer_service -am clean package -DskipTests

FROM amazoncorretto:11-alpine

EXPOSE 8080
COPY --from=build /workspace/recognizer_service/target/*.jar app.jar
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]