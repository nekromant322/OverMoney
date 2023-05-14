FROM maven:3.6.3-jdk-11-slim AS build

RUN mkdir -p /dto
WORKDIR /dto
COPY /dto/pom.xml /dto
COPY /dto/src /dto/src
RUN mvn -B -f pom.xml clean install -DskipTests -Dcheckstyle.skip

RUN mkdir -p /recognizer
WORKDIR /recognizer
COPY /recognizer_service/pom.xml /recognizer
COPY /recognizer_service/src /recognizer/src
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM openjdk:11-jdk-slim
COPY --from=build /recognizer/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]