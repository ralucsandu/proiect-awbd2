FROM openjdk:11-jdk-slim

WORKDIR /app

COPY target/book-service-1.0-SNAPSHOT.jar /app/book-service.jar

CMD ["java", "-jar", "book-service.jar"]
