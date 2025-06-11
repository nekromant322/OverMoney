FROM node:22-alpine as build

ARG BUILD_CMD=build
WORKDIR /app

COPY /frontend/app/package.json /frontend/app/package-lock.json ./
RUN npm install

COPY /frontend/app/. .

RUN npm run ${BUILD_CMD}

FROM nginx:1.23.1-alpine
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist /usr/share/nginx/html/app
COPY /frontend/app/docker/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
ENTRYPOINT ["nginx", "-g", "daemon off;"]
