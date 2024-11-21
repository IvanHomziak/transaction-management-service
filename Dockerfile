# Build stage
FROM openjdk:18-jdk-alpine AS builder

# Install curl and maven
RUN apk --no-cache add curl maven git

# Set environment variables for the main project
ENV DB_HOST=${DB_HOST}
ENV DB_NAME=${DB_NAME}
ENV DB_USER=${DB_USER}
ENV DB_PASS=${DB_PASS}

# Set working directory for transaction-common
WORKDIR /app/transaction-common

# Clone and build the transaction-common project
# Replace with your GitHub repository URL for transaction-common
RUN git clone https://github.com/IvanHomziak/transaction-common.git .
RUN mvn clean install

# Set working directory for the main project
WORKDIR /app/transaction-management-service

# Copy the pom.xml file to resolve dependencies
COPY pom.xml .

# Resolve dependencies
RUN mvn dependency:resolve

# Copy the source code to the working directory
COPY src src

# Build the project
RUN mvn package -DskipTests

# Production stage
FROM openjdk:18-jdk-alpine

# Expose port 9091
EXPOSE 9091

# Set working directory in the container
WORKDIR /usr/app

 Copy the built application from the builder stage
COPY --from=builder /app/transaction-management-service/target/transaction-management-service.jar .

# Run the application
ENTRYPOINT ["java", "-jar", "target/transaction-management-service.jar"]
