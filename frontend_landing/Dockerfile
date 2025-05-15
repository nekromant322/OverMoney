FROM node:18.20.1-alpine3.18 as build

ARG BUILD_CMD=build
WORKDIR /app

COPY /frontend_landing/package.json /frontend_landing/package-lock.json ./
RUN npm install

COPY /frontend_landing/. .

RUN npm run ${BUILD_CMD}

FROM nginx:1.23.1-alpine
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist /usr/share/nginx/html/landing
COPY /frontend_landing/docker/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
ENTRYPOINT ["nginx", "-g", "daemon off;"]
