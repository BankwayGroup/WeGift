# Use Alpine version of OpenJDK 8
FROM openjdk:8-jre-alpine3.9

WORKDIR /app

# Copy project files
COPY . .

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Install OpenJDK 8
RUN apk add --no-cache openjdk8

# Run Gradle build to generate the fat JAR
RUN ./gradlew build

# Check if the JAR file exists
RUN ls -l build/libs

# Copy the generated JAR to the container
COPY build/libs/app.jar /app.jar

# Run the application
CMD ["java", "-jar", "/app.jar"]
