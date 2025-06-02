# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy your entire Evert-Organizer folder contents into /app in container
COPY ./Evert-Organizer /app

# Make the Maven wrapper executable
RUN chmod +x ./mvnw

# Build your app inside the container
RUN ./mvnw clean package -DskipTests

# Expose port your Spring Boot app will run on (default 8080)
EXPOSE 8080

# Run the jar file produced by Maven
CMD ["java", "-jar", "target/*.jar"]
