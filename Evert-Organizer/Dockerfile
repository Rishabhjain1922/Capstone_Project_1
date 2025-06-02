# Use an official OpenJDK 17 runtime as the base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the Spring Boot app (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Expose the default Spring Boot port
EXPOSE 8080

# Run the Spring Boot application (use shell form for wildcard)
CMD java -jar target/*.jar
