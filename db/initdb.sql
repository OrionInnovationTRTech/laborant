CREATE TABLE IF NOT EXISTS lab(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) NOT NULL,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    host varchar(255) NOT NULL,
    port INT
)