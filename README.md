# Transaction Management Service

The **Transaction Management Service** is a Spring Boot application designed for handling financial transactions. It integrates with Kafka for message streaming, Redis for caching, and MySQL for persistent data storage. The service supports RESTful APIs for creating and retrieving transactions, along with Kafka-based asynchronous communication for processing transactions.

---

## Features

- **Transaction Management**: Create and retrieve financial transactions with REST APIs.
- **Redis Integration**: Utilize Redis for efficient caching of transaction data.
- **Kafka Messaging**: Produce and consume transaction events for distributed processing.
- **MySQL Persistence**: Store transaction data securely in a relational database.
- **Exception Handling**: Global exception handling for better error management.

---

## Technologies Used

- **Java 17**: Core programming language.
- **Spring Boot**: Framework for building the application.
- **Spring Data JPA**: ORM for database interactions.
- **Spring Kafka**: Kafka producer and consumer for event handling.
- **Redis**: Caching layer for improved performance.
- **MySQL**: Relational database management system.
- **Docker**: Containerization for the application and its dependencies.
- **Lombok**: Simplify Java code with annotations.

---

## Prerequisites

1. **Java 17**: Ensure JDK 17 is installed.
2. **Maven**: For building the project.
3. **Docker**: To run the application and its dependencies in containers.
4. **Kafka**: Kafka cluster setup for message streaming.
5. **Redis**: Redis setup with Sentinel for high availability.

---

## Setup and Installation

### 1. Clone the Repository

```bash
git clone https://github.com/IvanHomziak/bankingapp-transaction-ms.git
cd bankingapp-transaction-ms
```

### 2. Configure Environment Variables

Set up the environment variables in an `.env` file or export them in your shell:

```plaintext
DB_HOST=mysql-tms-c
DB_NAME=transactiondatabase
DB_USER=root
DB_PASS=admin1234
```

### 3. Build the Project

Use Maven to build the project:

```bash
mvn clean install
```

### 4. Run the Application

Run the application locally:

```bash
java -jar target/bankingapp-transaction-ms.jar
```

---

## Running with Docker

### 1. Start the Dependencies

Use the provided `docker-compose.yml` file to start the MySQL, Kafka, and Redis services:

```bash
docker-compose up -d
```

### 2. Build and Run the Service

Build the Docker image for the service:

```bash
docker build -t bankingapp-transaction-ms .
```

Start the service container:

```bash
docker-compose up --build
```

---

## REST API Endpoints

### Transaction Endpoints

| Method | Endpoint                  | Description                |
|--------|---------------------------|----------------------------|
| POST   | `/api/transaction`        | Create a new transaction   |
| GET    | `/api/transaction/{uuid}` | Retrieve a transaction by UUID |

---

## Kafka Integration

### Topics

- **`transfer-transactions-topic`**: Used for sending transaction messages.
- **`transaction-results-topic`**: Used for receiving transaction status updates.

### Producer and Consumer Configuration

- **Producer**: Publishes transaction events to the `transfer-transactions-topic`.
- **Consumer**: Listens to messages from the `transaction-results-topic`.

---

## Database Schema

### Transactions Table

| Column Name           | Type           | Description                  |
|-----------------------|----------------|------------------------------|
| `transaction_id`      | BIGINT         | Primary key                  |
| `transaction_uuid`    | VARCHAR        | Unique identifier for a transaction |
| `sender_uuid`         | VARCHAR        | Sender's account UUID        |
| `receiver_uuid`       | VARCHAR        | Receiver's account UUID      |
| `amount`              | DOUBLE         | Transaction amount           |
| `transaction_status`  | ENUM           | Status of the transaction    |
| `transaction_date`    | TIMESTAMP      | Date and time of the transaction |
| `last_update`         | TIMESTAMP      | Last updated timestamp       |

---

## Exception Handling

Centralized exception handling is implemented for:
- **TransactionNotFoundException**: Handles missing transactions.
- **TransactionFailedException**: Handles transaction failures.

---

## Contribution

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes and push them to your branch.
4. Create a pull request for review.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

---

## Contact

For issues or feature requests, feel free to create an issue in the [GitHub repository](https://github.com/IvanHomziak/bankingapp-transaction-ms/issues).