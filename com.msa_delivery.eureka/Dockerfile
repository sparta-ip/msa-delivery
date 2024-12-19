# Base image
FROM openjdk:17-jdk-slim

# curl 설치
RUN apt-get update && apt-get install -y curl && apt-get clean

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY build/libs/*SNAPSHOT.jar eureka-service.jar

# Expose the service port
EXPOSE 19090

# Run the application
ENTRYPOINT ["java", "-jar", "eureka-service.jar"]
