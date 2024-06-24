FROM openjdk:11-jdk-slim

WORKDIR /app

COPY target/gateway-1.0-SNAPSHOT.jar /app/gateway.jar

CMD ["java", "-jar", "gateway.jar"]
