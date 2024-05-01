# Use the official OpenJDK 8 image with Alpine Linux
FROM openjdk:8-jre-alpine3.9

# Set working directory
WORKDIR /app

# Copy the project files
COPY . .

# Build the fat JAR
RUN ./gradlew fatJar

# Default command to run the fat JAR
CMD ["java", "-jar", "build/libs/app.jar"]
