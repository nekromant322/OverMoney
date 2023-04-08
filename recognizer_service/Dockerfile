FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir -p /recognizer
WORKDIR /recognizer
COPY pom.xml /recognizer
COPY src /recognizer/src
RUN mvn -B -f pom.xml clean package -DskipTests

FROM openjdk:11-jdk-slim
COPY --from=build /recognizer/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]