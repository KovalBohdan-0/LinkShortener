#Build
FROM maven:3.9.0-sapmachine-17 AS build
COPY . .
RUN mvn clean package -DskipTests -P render

#Run
FROM openjdk:20-ea-17-jdk
COPY --from=build /target/LinkShortener-0.0.1-SNAPSHOT.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]
