CREATE DATABASE IF NOT EXISTS transactiondatabase;

USE transactiondatabase;

-- Create the `client` table
CREATE TABLE IF NOT EXISTS client (
                                      client_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Primary key
                                      uuid VARCHAR(255) NOT NULL                    -- UUID of the client
);

-- Create the `transaction` table
CREATE TABLE IF NOT EXISTS transaction (
                                           transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Primary key
                                           account_number VARCHAR(255),                       -- Account number
                                           amount DOUBLE,                                     -- Transaction amount
                                           transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Date of transaction
                                           transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER'),  -- Transaction type enum
                                           client_id BIGINT NOT NULL,                         -- Foreign key to `client`
                                           FOREIGN KEY (client_id) REFERENCES client(client_id)  -- Define foreign key constraint
);

-- Insert some sample data for the client
INSERT INTO client (uuid) VALUES ('123e4567-e89b-12d3-a456-426614174000');

-- Insert some sample transactions for the client with client_id 1
INSERT INTO transaction (account_number, amount, transaction_type, client_id)
VALUES ('ACC12345', 100.00, 'DEPOSIT', 1),
       ('ACC12345', 50.00, 'WITHDRAWAL', 1);
