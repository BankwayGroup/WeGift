# Use the official OpenJDK 8 image with Alpine Linux
FROM openjdk:8-jre-alpine3.9

# Set the working directory inside the container
WORKDIR /app

# Copy all JAR files from build/libs to the container
COPY build/libs/*.jar /app/

# If you have multiple JAR files and you want to specify a specific one, you can do so with a more explicit COPY command:
# COPY build/libs/app.jar /app/app.jar

# Set the default command to run the JAR file
CMD ["java", "-jar", "/app/app.jar"]
