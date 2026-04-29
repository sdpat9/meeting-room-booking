# Meeting Room Booking System

Backend application for booking meeting rooms built with Java, Spring Boot, Spring Data JPA, and PostgreSQL.

## Description

This project is a REST API for managing meeting rooms, users, and bookings.

The system allows:
- creating and managing meeting rooms
- creating and managing users
- booking rooms for a selected time period
- preventing booking conflicts
- searching and filtering available rooms
- viewing bookings with pagination
- validating request data
- handling errors with structured responses
- role-based access control (ADMIN / USER)
- smart room recommendation

## Technologies

- Java 17+
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger / OpenAPI
- Docker Compose
- JUnit 5 + Mockito

## Main Features

### Rooms
- create room (ADMIN only)
- update room (ADMIN only)
- deactivate room (ADMIN only)
- get room by id
- list all rooms
- search available rooms by time range
- filter available rooms by capacity
- paginate and sort available rooms
- recommend best available room

### Users
- create user (ADMIN only)
- update user (ADMIN only)
- deactivate user (ADMIN only)
- get user by id
- list all users
- unique email validation

### Bookings
- create booking
- cancel booking (owner or ADMIN)
- get booking by id
- list room bookings with pagination
- list user bookings with pagination
- prevent room booking conflicts
- prevent user booking conflicts

## Role Model

The system uses a simple role-based access model.

Roles:
- ADMIN
- USER

ADMIN can:
- manage rooms
- manage users
- view and cancel any booking

USER can:
- create bookings
- view own bookings
- cancel own bookings
- search available rooms

Note: authentication is not implemented yet. Actor is passed via request parameters (adminId, actorId).

## Smart Room Recommendation

The system can recommend the best available room.

Endpoint:
GET /api/rooms/recommend

Parameters:
- start
- end
- participants

Logic:
- finds available rooms
- filters by capacity
- returns the smallest suitable room

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

Swagger UI:
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
- POST /api/rooms?adminId=1  
- GET /api/rooms/{id}  
- PUT /api/rooms/{id}?adminId=1  
- DELETE /api/rooms/{id}?adminId=1  
- GET /api/rooms/available?start=...&end=...  
- GET /api/rooms/recommend?start=...&end=...&participants=...  

### Users
- GET /api/users  
- POST /api/users?adminId=1  
- GET /api/users/{id}  
- PUT /api/users/{id}?adminId=1  
- DELETE /api/users/{id}?adminId=1  

### Bookings
- POST /api/bookings  
- GET /api/bookings/{id}  
- GET /api/bookings/room/{roomId}  
- GET /api/bookings/user/{userId}  
- DELETE /api/bookings/{bookingId}?actorId=1  

## Request Examples

### Create Room

```json
{
  "name": "Conference Room A",
  "capacity": 10,
  "active": true
}
```

### Create User

```json
{
  "name": "Sergey",
  "email": "sergey@example.com",
  "active": true,
  "role": "USER"
}
```

### Create Booking

```json
{
  "roomId": 1,
  "userId": 1,
  "title": "Team meeting",
  "participantsCount": 5,
  "start": "2026-03-30T10:00:00",
  "end": "2026-03-30T11:00:00"
}
```

## Project Structure

src/main/java/by/bsuir/meetingroombooking  
├── controller  
├── dto  
├── exception  
├── mapper  
├── model  
├── repository  
└── service  

## Tests

Run tests:

mvn test

## Future Improvements

- authentication and authorization (Spring Security + JWT)
- Flyway database migrations
- logging
- Docker image for backend
- frontend client
- caching

## Author

Student backend project (Java + Spring Boot)