# Use an official OpenJDK runtime as a base image
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container at /app
COPY target/book-service-1.0-SNAPSHOT.jar /app/book-service.jar

# Specify the command to run your application
CMD ["java", "-jar", "book-service.jar"]
