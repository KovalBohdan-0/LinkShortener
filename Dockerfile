#Build
FROM maven:3.9.0-eclipse-temurin-17-alpine AS build
COPY . .
ARG enviroment
RUN mvn clean package -DskipTests -P $enviroment

#Run
FROM openjdk:20-ea-17-slim
COPY --from=build /target/LinkShortener-0.0.1-SNAPSHOT.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]
