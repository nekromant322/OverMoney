FROM maven:3.6.3-jdk-11-slim AS build

RUN mkdir -p /dto
WORKDIR /dto
COPY /dto/pom.xml /dto
COPY /dto/src /dto/src
RUN mvn -B -f pom.xml clean install -DskipTests

RUN mkdir -p /orchestrator
WORKDIR /orchestrator
COPY /orchestrator_service/pom.xml /orchestrator
COPY /orchestrator_service/src /orchestrator/src
RUN mvn -B -f pom.xml clean package -DskipTests

FROM openjdk:11-jdk-slim
COPY --from=build /orchestrator/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]