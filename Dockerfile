# Stage 1: Build the Maven project using Java 25
FROM maven:3-eclipse-temurin-25-alpine AS build
WORKDIR /app

# Copy dependency configurations to take advantage of caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Light-weight runtime environment using Java 25
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app

# Copy the built jar file from the previous stage
COPY --from=build /app/target/*.jar atto-bot.jar

# Execute your Telegram Bot console app
ENTRYPOINT ["java", "-jar", "atto-bot.jar"]
