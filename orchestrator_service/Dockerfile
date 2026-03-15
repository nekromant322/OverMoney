# syntax=docker/dockerfile:1.6
FROM maven:3.6.3-jdk-11-slim AS build

# Сначала копируем .git вручную
WORKDIR /workspace

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
  mvn -B -pl orchestrator_service -am clean package -DskipTests \

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]