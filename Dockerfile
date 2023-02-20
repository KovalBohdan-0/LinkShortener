FROM openjdk:20-ea-17-jdk
ADD /target/LinkShortener-0.0.1-SNAPSHOT.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]