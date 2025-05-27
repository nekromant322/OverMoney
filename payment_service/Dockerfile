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

WORKDIR /workspace/payment_service
COPY /payment_service/pom.xml /workspace/payment_service
COPY /payment_service/src /workspace/payment_service/src
RUN cp -r /workspace/.git /workspace/payment_service/.git
RUN mvn -B clean package -DskipTests -Dcheckstyle.skip

# Stage 2: Runtime
FROM amazoncorretto:11-alpine

EXPOSE 8084
COPY --from=build /workspace/payment_service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]