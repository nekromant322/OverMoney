# you can use --cache-from to speed up whisper build
# maybe you can use another image but libwhisper.so waht libc.so.6
# we can't use slim images
# and I cba build it in slim image
FROM openjdk:17-bullseye as whisper
RUN apt-get update -y \
    && apt-get install -y wget \
    && wget https://ggml.ggerganov.com/ggml-model-whisper-base.bin

FROM maven:3.6.3-openjdk-17-slim AS build
RUN mkdir -p /dto
WORKDIR /dto
COPY /dto/pom.xml /dto
COPY /dto/src /dto/src
RUN mvn -B -f pom.xml clean install -DskipTests -Dcheckstyle.skip

RUN mkdir -p /recognizer
WORKDIR /recognizer
COPY /recognizer_service/pom.xml /recognizer
COPY /recognizer_service/src /recognizer/src
RUN apt-get update -y \
    && apt-get install -y build-essential git g++
RUN git clone https://github.com/GiviMAD/whisper-jni \
    && cd whisper-jni \
    && git submodule update --init \
    && ./build_debian.sh \
    && cp -a src/main/resources/. ../src/main/resources/
RUN mvn -B -f pom.xml clean package -DskipTests -Dcheckstyle.skip

FROM openjdk:17-bullseye
RUN apt-get -y update \
    && apt-get -y upgrade \
    && apt-get install -y --no-install-recommends ffmpeg
COPY --from=whisper ggml-model-whisper-base.bin ggml-model-whisper-base.bin
EXPOSE 8080
COPY --from=build /recognizer/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]

