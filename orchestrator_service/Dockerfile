# syntax=docker/dockerfile:1.6

FROM maven:3.6.3-jdk-11-slim AS build

# Сначала копируем .git вручную
WORKDIR /workspace

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests -pl :orchestrator_service -am


FROM amazoncorretto:11-alpine

WORKDIR /app

EXPOSE 8081
COPY --from=build /workspace/orchestrator/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]