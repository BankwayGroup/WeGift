# Use Alpine version of OpenJDK 8
FROM openjdk:8-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the project files into the container
COPY . .

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Install the OpenJDK development package
RUN apk add --no-cache openjdk8-jdk

# Run Gradle build to generate the fat JAR
RUN ./gradlew build

# Copy the generated JAR to the container
COPY build/libs/*.jar /app/app.jar

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]
