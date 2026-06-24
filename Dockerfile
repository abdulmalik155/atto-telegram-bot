# Stage 1: Build using standard Maven with Java 21
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app

# Copy your complete project code directory right away
COPY . .

# Run direct clean package build
RUN mvn clean package -DskipTests

# Stage 2: Clean runtime environment using Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# FIXED: Targets the exact custom final name enforced by your pom.xml
COPY --from=build /app/target/atto-bot.jar atto-bot.jar

# Start your Telegram Bot console service
ENTRYPOINT ["java", "-jar", "atto-bot.jar"]
