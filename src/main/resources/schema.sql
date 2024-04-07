CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  varchar(100),
    email varchar(320)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         varchar(100),
    description  varchar(255),
    is_available boolean,
    user_id      BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY (user_id) REFERENCES users (id),
    UNIQUE (id)
);
