# Stage 1: Build
FROM maven:3.6.3-openjdk-11-slim AS build

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

WORKDIR /workspace/recognizer_service
COPY /recognizer_service/pom.xml /workspace/recognizer_service
COPY /recognizer_service/src /workspace/recognizer_service/src
RUN cp -r /workspace/.git /workspace/recognizer_service/.git
RUN mvn -B clean package -DskipTests -Dcheckstyle.skip

# Stage 2: Runtime
FROM openjdk:11-bullseye

RUN apt-get -y update \
    && apt-get -y upgrade \
    && apt-get install -y --no-install-recommends ffmpeg

EXPOSE 8080
COPY --from=build /workspace/recognizer_service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
