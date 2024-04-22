FROM node:18.20.1-alpine3.18 as build
WORKDIR /app
COPY /frontend/package.json /frontend/package-lock.json ./
RUN npm install
COPY /frontend/. .
RUN npm run build

FROM nginx:1.23.1-alpine
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/build /usr/share/nginx/html
COPY /frontend/docker/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
COPY /frontend/docker/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /usr/app/build /usr/share/nginx/html