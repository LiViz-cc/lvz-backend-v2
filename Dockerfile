# Stage 1: Use the official Maven image to build the project
FROM maven:3.8.2-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Use Maven to build the project and generate the JAR file
RUN mvn clean install

# Stage 2: Use the official JDK 17 base image
FROM openjdk:17-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the YAML configuration file into the image
COPY src/main/resources/application.yml.templete application.yml

# Replace the environment variables in the YAML configuration file
RUN sed -i "s|DB_URI|${DB_URI}|g" application.yml \
  && sed -i "s|TEST_USERNAME|${TEST_USERNAME}|g" application.yml \
  && sed -i "s|TEST_PASSWORD|${TEST_PASSWORD}|g" application.yml \
  && sed -i "s|LIVIZ_JWT_SECRET_KEY|${LIVIZ_JWT_SECRET_KEY}|g" application.yml

# Expose ports
EXPOSE 8080 8081

# Define the entrypoint for running the application
ENTRYPOINT ["java", "-jar", "app.jar"]
