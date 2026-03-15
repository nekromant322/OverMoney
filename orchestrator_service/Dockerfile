# syntax=docker/dockerfile:1.6

FROM maven:3.6.3-jdk-11-slim AS build

WORKDIR /workspace

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl orchestrator_service -am \
    clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine

EXPOSE 8081
COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]