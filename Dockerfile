# Stage 1: Build using standard Maven with Java 25
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app

# Copy your complete project code directory right away
COPY . .

# Run direct clean package build
RUN mvn clean package -DskipTests

# Stage 2: Clean runtime environment using Java 25
FROM eclipse-temurin:25-jre
WORKDIR /app

# Safely carry over the compiled jar artifact 
COPY --from=build /app/target/*.jar atto-bot.jar

# Start your Telegram Bot console service
ENTRYPOINT ["java", "-jar", "atto-bot.jar"]
