FROM node:18.20.1-alpine3.18 as build
WORKDIR /usr/app
COPY . /usr/app
RUN npm ci --noproxy registry.npmjs.org
RUN npm run build

FROM nginx:1.23.1-alpine
EXPOSE 80
COPY /frontend/docker/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /usr/app/build /usr/share/nginx/html