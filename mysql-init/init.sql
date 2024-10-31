-- Create the `transactiondatabase` database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS transactiondatabase;

-- Use the `transactiondatabase` database
USE transactiondatabase;

-- Create the `transaction` table
CREATE TABLE IF NOT EXISTS transaction (
                                           transaction_id BIGINT(20) AUTO_INCREMENT PRIMARY KEY,     -- Primary key for transaction
                                           transaction_uuid VARCHAR(255) NOT NULL,                   -- UUID of the transaction
                                           sender_uuid VARCHAR(255) NOT NULL,                        -- UUID of the sender
                                           receiver_uuid VARCHAR(255) NOT NULL,                      -- UUID of the receiver
                                           amount DOUBLE NOT NULL,                                   -- Amount of transaction
                                           transaction_status ENUM('NEW', 'COMPLETED', 'FAILED') NOT NULL,  -- Enum for transaction status
                                           transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,  -- Enum for transaction type
                                           transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,     -- Creation timestamp
                                           last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- Last update timestamp
);

-- Example insert statements (optional) for testing purposes
INSERT INTO transaction (transaction_uuid, sender_uuid, receiver_uuid, amount, transaction_status, transaction_type)
VALUES ('123e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-23423423', '3840ade4-f8ad-4829-a7af-b88192d52243', 100.00, 'NEW', 'TRANSFER');

INSERT INTO transaction (transaction_uuid, sender_uuid, receiver_uuid, amount, transaction_status, transaction_type)
VALUES ('123e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-42661417qweqwe', '3840ade4-f8ad-4829-a7af-b88192d52243', 50.00, 'COMPLETED', 'WITHDRAWAL');
