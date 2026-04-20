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

COPY recognizer_service/pom.xml /workspace/recognizer_service/pom.xml
COPY recognizer_service/src /workspace/recognizer_service/src

RUN cp -r /workspace/.git /workspace/recognizer_service/.git

WORKDIR /workspace/recognizer_service
RUN mvn -B clean package -DskipTests -Dcheckstyle.skip

# Stage 2: Runtime
FROM eclipse-temurin:11

RUN apt-get update \
    && apt-get -y upgrade \
    && apt-get install -y --no-install-recommends ffmpeg \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080
COPY --from=build /workspace/recognizer_service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]