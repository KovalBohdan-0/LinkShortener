# Build
FROM node:current-alpine3.17 as build
WORKDIR /usr/local/app
COPY ./ /usr/local/app/
RUN npm install
ARG env=render
RUN npm run build -- --configuration $env

# Run
FROM nginx:stable-alpine
ARG env=render
COPY default.$env.conf /etc/nginx/conf.d/default.conf
COPY --from=build /usr/local/app/dist/frontend /usr/share/nginx/html
