FROM maven:3.6.3-jdk-11-slim AS build

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

RUN mkdir -p /telegram_bot
WORKDIR /telegram_bot
COPY /telegram_bot_service/pom.xml /telegram_bot
COPY /telegram_bot_service/src /telegram_bot/src
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM amazoncorretto:11-alpine
COPY --from=build /telegram_bot/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]