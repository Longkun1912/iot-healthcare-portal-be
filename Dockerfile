# Use the base image with JDK 18
FROM openjdk:18

LABEL mentainer="rongvang1912tc@gmail.com"

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY target/iot-health-0.0.1-SNAPSHOT.jar /app/iot-health.jar

# Expose the port your app runs on
EXPOSE 8449

# Command to run the application when the container starts
CMD ["java", "-jar", "iot-health.jar"]


