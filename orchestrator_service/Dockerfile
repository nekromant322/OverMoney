FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /build

# 1. копируем pom для кеша зависимостей
COPY pom.xml .

COPY dto/pom.xml dto/
COPY mask_log_starter/pom.xml mask_log_starter/
COPY actuator_git_info_starter/pom.xml actuator_git_info_starter/
COPY orchestrator_service/pom.xml orchestrator_service/

# 3. копируем исходники
COPY dto/src dto/src
COPY mask_log_starter/src mask_log_starter/src
COPY actuator_git_info_starter/src actuator_git_info_starter/src
COPY orchestrator_service/src orchestrator_service/src

# 4. билдим только orchestrator
RUN mvn -B -pl orchestrator_service -am clean package -DskipTests -Dcheckstyle.skip


# ---------- runtime ----------
FROM amazoncorretto:11-alpine
WORKDIR /app

COPY --from=build /build/orchestrator_service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]