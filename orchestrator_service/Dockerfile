FROM maven:3.6.3-jdk-11-slim AS build

RUN mkdir -p /dto
WORKDIR /dto
COPY /dto/pom.xml /dto
COPY /dto/src /dto/src
RUN mvn -B -f pom.xml clean install -DskipTests -Dcheckstyle.skip

RUN mkdir -p /mask_log_starter
WORKDIR /mask_log_starter
COPY /mask_log_starter/pom.xml /mask_log_starter
COPY /mask_log_starter/src /mask_log_starter/src
RUN mvn -B -f pom.xml clean install -DskipTests -Dcheckstyle.skip

RUN mkdir -p /orchestrator
WORKDIR /orchestrator
COPY /orchestrator_service/pom.xml /orchestrator
COPY /orchestrator_service/src /orchestrator/src
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine
COPY --from=build /orchestrator/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]