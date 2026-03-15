FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /workspace

COPY orchestrator_service/pom.xml .
COPY orchestrator_service/src src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl orchestrator_service -am \
    clean package -DskipTests -Dcheckstyle.skip


FROM amazoncorretto:11-alpine
WORKDIR /app

COPY --from=build /workspace/orchestrator_service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]