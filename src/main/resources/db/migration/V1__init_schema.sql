CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    participants_count INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES rooms(id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_booking_room_time ON bookings(room_id, start_time, end_time);