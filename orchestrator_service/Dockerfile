FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /build

COPY . .

RUN mvn -B -pl orchestrator_service -am clean package \
    -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine
WORKDIR /app

COPY --from=build /build/orchestrator_service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]