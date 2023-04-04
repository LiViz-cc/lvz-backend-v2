ARG DB_URI
ARG TEST_USERNAME
ARG TEST_PASSWORD
ARG JWT_SECRET

ENV DB_URI=${DB_URI}
ENV TEST_USERNAME=${TEST_USERNAME}
ENV TEST_PASSWORD=${TEST_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}

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
COPY src/main/resources/application.yml.example application.yml

# Expose ports
EXPOSE 8080 8081

# Define the entrypoint for running the application
ENTRYPOINT ["java", "-jar", "app.jar"]
