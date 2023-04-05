
RUN mkdir -p /overmoney
WORKDIR /overmoney
COPY eureka_server/pom.xml eureka_server/pom.xml
COPY orchestrator_service/pom.xml orchestrator_service/pom.xml
COPY recognizer_service/pom.xml recognizer_service/pom.xml
COPY pom.xml /overmoney
RUN mvn -B -e -C org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /overmoney
COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /overmoney /overmoney/app
COPY eureka_server/src /overmoney/eureka_server/src
COPY orchestrator_service/src /overmoney/orchestrator_service/src
COPY recognizer_service/src /overmoney/recognizer_service/src
RUN mvn -B -e -o clean install -DskipTests=true

FROM openjdk:11-jdk-slim
WORKDIR /overmoney
COPY --from=build /overmoney/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]