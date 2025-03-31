# Use a minimal Java 21 image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory in container
WORKDIR /app

# Copy built JAR and .env file
COPY target/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8877

# Set Spring Boot to load .env manually via your Dotenv integration
ENV SPRING_CONFIG_LOCATION=classpath:/application.properties,optional:file:.env

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
