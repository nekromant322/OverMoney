# Stage 1: Build
FROM maven:3.6.3-openjdk-11-slim AS build

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

COPY telegram_bot_service/pom.xml /workspace/telegram_bot/pom.xml
COPY telegram_bot_service/src /workspace/telegram_bot/src

WORKDIR /workspace/telegram_bot
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

# Stage 2: Runtime
FROM amazoncorretto:11-alpine

EXPOSE 8082
COPY --from=build /workspace/telegram_bot/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]