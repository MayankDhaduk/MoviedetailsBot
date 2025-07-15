FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory where pom.xml is located
WORKDIR /main/bot
# Copy all files
COPY . .

# If your pom.xml is in a subdirectory, like /MoviedetailsBot
# WORKDIR /main/bot

# Build the project
RUN mvn clean package -DskipTests

# Run the JAR (update the jar name)
CMD ["java", "-jar", "target/MovieBot-1.0.jar"]
