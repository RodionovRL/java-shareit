CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(254)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(2500)                           NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    available   BOOLEAN,
    owner_id    BIGINT                                  NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    request_id  BIGINT REFERENCES requests (id) ON DELETE RESTRICT,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id    BIGINT                                  NOT NULL REFERENCES items (id) ON DELETE RESTRICT,
    booker_id  BIGINT                                  NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    status     VARCHAR(8) DEFAULT 'WAITING',
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR                                 NOT NULL,
    item_id   BIGINT                                  NOT NULL REFERENCES items (id) ON DELETE RESTRICT,
    author_id BIGINT                                  NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comment UNIQUE (id)
);