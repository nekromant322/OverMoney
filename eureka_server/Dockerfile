FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir -p /eureka
WORKDIR /eureka
COPY pom.xml /eureka
COPY src /eureka/src
RUN mvn -B -f pom.xml clean package -DskipTests

FROM openjdk:11-jdk-slim
COPY --from=build /eureka/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]