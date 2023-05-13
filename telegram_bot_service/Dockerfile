FROM maven:3.6.3-jdk-11-slim AS build

RUN mkdir -p /dto
WORKDIR /dto
COPY /dto/pom.xml /dto
COPY /dto/src /dto/src
RUN mvn -B -f pom.xml clean install -DskipTests

RUN mkdir -p /telegram_bot
WORKDIR /telegram_bot
COPY /telegram_bot_service/pom.xml /telegram_bot
COPY /telegram_bot_service/src /telegram_bot/src
RUN mvn -B -f pom.xml clean package -DskipTests

FROM openjdk:11-jdk-slim
COPY --from=build /telegram_bot/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]