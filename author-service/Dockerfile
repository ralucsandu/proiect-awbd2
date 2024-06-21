# Use an official OpenJDK runtime as a base image
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container at /app
COPY target/author-service-1.0-SNAPSHOT.jar /app/author-service.jar

# Specify the command to run your application
CMD ["java", "-jar", "author-service.jar"]
