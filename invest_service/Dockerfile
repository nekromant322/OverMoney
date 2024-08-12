FROM maven:3.6.3-openjdk-11-slim AS build

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

RUN mkdir -p /invest
WORKDIR /invest
COPY /invest_service/pom.xml /invest
COPY /invest_service/src /invest/src
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine
COPY --from=build /invest/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]