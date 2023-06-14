# you can use --cache-from to speed up whisper build
# maybe you can use another image but libwhisper.so waht libc.so.6
# we can't use slim images 
# and I cba build it in slim image
FROM openjdk:11-bullseye as whisper
RUN apt-get update -y \
    && apt-get install -y make git g++ tree wget \
    && git clone https://github.com/nekromant322/whisper.cpp \
    && cd whisper.cpp \
    && make libwhisper.so \
    && mkdir build \
    && cp libwhisper.so build \
    && cd build \
    && wget https://ggml.ggerganov.com/ggml-model-whisper-base.bin




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

FROM openjdk:11-bullseye
RUN apt-get -y update \
    && apt-get -y upgrade \
    && apt-get install -y --no-install-recommends ffmpeg
COPY --from=whisper /whisper.cpp/build/libwhisper.so /jni/libwhisper.so
COPY --from=whisper /whisper.cpp/build/ggml-model-whisper-base.bin /root/.cache/whisper/ggml-model-whisper-base.bin
EXPOSE 8080
ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/jni
COPY --from=build /recognizer/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]

