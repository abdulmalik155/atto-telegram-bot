# Stage 1: Build the application with Java 25
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy dependency structures and download them
COPY pom.xml .
COPY src ./src

# Run direct clean package build
RUN mvn clean package -DskipTests

# Stage 2: Balanced runtime environment using Java 25
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy the built "fat" executable bundle from stage 1
COPY --from=build /app/target/atto-bot.jar atto-bot.jar

# Execute application
ENTRYPOINT ["java", "-jar", "atto-bot.jar"]
