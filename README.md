# Meeting Room Booking System

Backend application for booking meeting rooms built with Java 17, Spring Boot, Spring Data JPA, and PostgreSQL.

## Description

This project is a REST API for managing meeting rooms, users, and room bookings.

The system allows:
- creating and managing meeting rooms
- creating and managing users
- booking rooms for a selected time period
- preventing booking conflicts
- searching for available rooms
- viewing bookings with pagination
- validating request data
- handling errors with structured responses

## Technologies

- Java 17
- Spring Boot 3.5.11
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger / OpenAPI
- Docker Compose

## Main Features

### Rooms
- create a room
- get room by id
- list all rooms
- deactivate a room
- search available rooms by time range

### Users
- create a user
- get user by id
- list all users
- deactivate a user

### Bookings
- create booking
- cancel booking
- list room bookings with pagination

## Business Rules

- a room must be active to be booked
- a user must be active to create a booking
- booking time must be valid: start < end
- booking cannot start in the past
- minimum booking duration is 15 minutes
- maximum booking duration is 8 hours
- room bookings cannot overlap
- a user cannot have overlapping bookings

## API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

## Running the Project

### 1. Clone the repository

git clone <your-repository-url>  
cd meeting-room-booking

### 2. Start PostgreSQL

docker compose up -d

### 3. Run the application

mvn spring-boot:run

или через IntelliJ (запустить MeetingRoomBookingApplication)

## Build

mvn clean install

## Example Endpoints

### Rooms
- GET /api/rooms  
- POST /api/rooms  
- GET /api/rooms/{id}  
- DELETE /api/rooms/{id}  
- GET /api/rooms/available?start=2026-03-30T10:00:00&end=2026-03-30T11:00:00  

### Users
- GET /api/users  
- POST /api/users  
- GET /api/users/{id}  
- DELETE /api/users/{id}  

### Bookings
- POST /api/bookings  
- GET /api/bookings/room/{roomId}?page=0&size=10&sortBy=start&direction=asc  
- DELETE /api/bookings/{bookingId}  

## Request Examples

### Create Room

{
  "name": "Conference Room A",
  "capacity": 10,
  "active": true
}

### Create User

{
  "name": "Sergey",
  "email": "sergey@example.com",
  "active": true
}

### Create Booking

{
  "roomId": 1,
  "userId": 1,
  "start": "2026-03-30T10:00:00",
  "end": "2026-03-30T11:00:00"
}

## Project Structure

src/main/java/by/bsuir/meetingroombooking  
├── controller  
├── dto  
├── exception  
├── mapper  
├── model  
├── repository  
└── service  

## Future Improvements

- get bookings by user  
- authentication and authorization  
- Flyway database migrations  
- frontend client  

## Author

Student backend project (Java + Spring Boot)
