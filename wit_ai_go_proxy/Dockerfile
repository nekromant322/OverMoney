# syntax=docker/dockerfile:1

FROM golang:1.20

RUN mkdir -p /app
WORKDIR /app


COPY /wit_ai_go_proxy /app

RUN apt-get update && apt-get -y install libopus-dev libopusfile-dev
RUN go mod download
# COPY *.go ./

RUN go build -o /wit_ai_go_proxy

EXPOSE 8080

CMD ["/wit_ai_go_proxy"]