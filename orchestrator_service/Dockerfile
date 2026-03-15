FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app

COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests -Dcheckstyle.skip


FROM amazoncorretto:11-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]