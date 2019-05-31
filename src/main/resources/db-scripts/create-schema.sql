CREATE TABLE customer(
    id INT PRIMARY KEY,
    personal_id VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(256) NOT NULL,
    phone VARCHAR(32),
    email VARCHAR(256)
);

CREATE TABLE account(
    account_number VARCHAR(20) PRIMARY KEY,
    customer_id INT NOT NULL,
    balance NUMERIC(8,2),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE transaction_history(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_from VARCHAR(20),
    account_to VARCHAR(20) NOT NULL,
    amount NUMERIC(8,2),
    FOREIGN KEY (account_from) REFERENCES account(account_number),
    FOREIGN KEY (account_to) REFERENCES account(account_number)
);

commit;