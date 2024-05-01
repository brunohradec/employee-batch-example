CREATE TABLE employee (
    id SERIAL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    welcome_email_status VARCHAR(50) NOT NULL,

    PRIMARY KEY (id)
);
