# Build stage
FROM openjdk:18-jdk-alpine

# Install required dependencies
RUN apk --no-cache add git curl maven

# Set environment variables for the main project
ENV DB_HOST=${DB_HOST}
ENV DB_NAME=${DB_NAME}
ENV DB_USER=${DB_USER}
ENV DB_PASS=${DB_PASS}

# Expose application port
EXPOSE 8085

# Set working directory
WORKDIR /app

# Clone and build bankingapp-common
RUN git clone https://github.com/IvanHomziak/bankingapp-common.git && \
    cd bankingapp-common && \
    mvn clean install -DskipTests

# Go back to /app directory
WORKDIR /app

# Copy project files (excluding `src` to use caching efficiently)
COPY pom.xml .


# Resolve dependencies (this speeds up builds by using caching)
RUN mvn dependency:resolve

# Copy the rest of the source code
COPY src src

# Build the project
RUN mvn clean package -DskipTests

# Run the application
ENTRYPOINT ["java", "-jar", "target/bankingapp-transaction-ms.jar"]
