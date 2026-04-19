# syntax=docker/dockerfile:1

FROM golang:1.20 AS build

WORKDIR /app

COPY wit_ai_go_proxy/go.mod wit_ai_go_proxy/go.sum ./
RUN go mod download

COPY wit_ai_go_proxy/. .

RUN apt-get update \
    && apt-get install -y --no-install-recommends libopus-dev libopusfile-dev \
    && rm -rf /var/lib/apt/lists/*

RUN go build -o /wit_ai_go_proxy .

EXPOSE 8080

CMD ["/wit_ai_go_proxy"]