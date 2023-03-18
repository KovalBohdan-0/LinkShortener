# Build
FROM node:current-alpine3.17 as build
WORKDIR /usr/local/app
COPY ./ /usr/local/app/
RUN npm install
ARG enviroment
RUN npm run build -- --configuration $enviroment

# Run
FROM nginx:stable-alpine
ARG enviroment
COPY default.$enviroment.conf /etc/nginx/conf.d/default.conf
COPY --from=build /usr/local/app/dist/frontend /usr/share/nginx/html