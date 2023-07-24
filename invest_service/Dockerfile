FROM maven:3.6.3-openjdk-11-slim AS build
RUN mkdir -p /dto
WORKDIR /dto
COPY /dto/pom.xml /dto
COPY /dto/src /dto/src
RUN mvn -B -f pom.xml clean install -DskipTests -Dcheckstyle.skip

RUN mkdir -p /invest
WORKDIR /invest
COPY /invest_service/pom.xml /invest
COPY /invest_service/src /invest/src
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM openjdk:11-bullseye
RUN apt-get -y update \
    && apt-get -y upgrade \
    && apt-get install -y --no-install-recommends ffmpeg
EXPOSE 8080
COPY --from=build /invest/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]