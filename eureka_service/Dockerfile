FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir -p /eureka
WORKDIR /eureka
COPY /eureka_service/pom.xml /eureka
COPY /eureka_service/src /eureka/src
RUN mvn -B -f pom.xml clean package -DskipTests

FROM openjdk:11-jdk-slim
COPY --from=build /eureka/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]