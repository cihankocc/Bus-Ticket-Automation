-- ============================================================
-- Otomasyon Projesi - rename.sql (Tam İngilizce)
-- ============================================================

DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Seat;
DROP TABLE IF EXISTS Dpt;
DROP TABLE IF EXISTS Bus;
DROP TABLE IF EXISTS User;

CREATE TABLE User (
    user_id    INT          AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('passenger','owner') NOT NULL DEFAULT 'passenger'
);

CREATE TABLE Bus (
    bus_id  INT         AUTO_INCREMENT PRIMARY KEY,
    plate   VARCHAR(20) NOT NULL UNIQUE,
    captain VARCHAR(100)
);

CREATE TABLE Dpt (
    departure_id    INT            AUTO_INCREMENT PRIMARY KEY,
    departure_city  VARCHAR(100)   NOT NULL,
    arrival_city    VARCHAR(100)   NOT NULL,
    date            DATE           NOT NULL,
    time            TIME           NOT NULL,
    ticket_price    DECIMAL(10,2)  NOT NULL,
    bus_id          INT            NOT NULL,
    available_seats INT            NOT NULL DEFAULT 40,
    route_info      TEXT,
    FOREIGN KEY (bus_id) REFERENCES Bus(bus_id) ON DELETE CASCADE
);

CREATE TABLE Seat (
    seat_id     INT  AUTO_INCREMENT PRIMARY KEY,
    seat_number INT  NOT NULL,
    status      ENUM('available','booked','reserved') NOT NULL DEFAULT 'available',
    dpt_id      INT  NOT NULL,
    UNIQUE KEY uq_seat (seat_number, dpt_id),
    FOREIGN KEY (dpt_id) REFERENCES Dpt(departure_id) ON DELETE CASCADE
);

CREATE TABLE Booking (
    booking_id  INT       AUTO_INCREMENT PRIMARY KEY,
    user_id     INT       NOT NULL,
    dpt_id      INT       NOT NULL,
    seat_number INT       NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_booking (dpt_id, seat_number),
    FOREIGN KEY (user_id) REFERENCES User(user_id)     ON DELETE CASCADE,
    FOREIGN KEY (dpt_id)  REFERENCES Dpt(departure_id) ON DELETE CASCADE
);