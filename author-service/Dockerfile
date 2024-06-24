FROM openjdk:11-jdk-slim

WORKDIR /app

COPY target/author-service-1.0-SNAPSHOT.jar /app/author-service.jar

CMD ["java", "-jar", "author-service.jar"]
