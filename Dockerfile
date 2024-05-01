# Use the official OpenJDK 8 image with Alpine Linux
FROM openjdk:11

# Copy the fat JAR file into the container
COPY build/libs/app.jar /app.jar

# Set the default command to run the JAR file
CMD ["java", "-jar", "/app.jar"]
