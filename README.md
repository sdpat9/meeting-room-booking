# Meeting model.Room model.Booking System

Backend application for managing meeting rooms and bookings.

## 📌 Description

This project is an information system for booking meeting rooms.

The current implementation includes:

- model.Room domain model
- model.Booking domain model
- model.Booking status management (ACTIVE / CANCELLED)
- Validation logic (capacity, booking duration, time constraints)
- Overlap checking for bookings

The project is currently implemented as a core domain logic module (without REST API).

---

## 🏗 Architecture (Current Stage)

Domain layer:
- `model.Room`
- `model.Booking`
- `model.Status`

Business logic:
- model.Booking validation
- Overlap detection
- model.Booking cancellation

---

## 🚀 Planned Features

- Spring Boot integration
- REST API (CRUD for rooms and bookings)
- Database integration (PostgreSQL)
- DTO layer
- Exception handling
- Unit tests
- Docker support

---

## 🛠 Tech Stack (Current)

- Java 17+
- Git
- IntelliJ IDEA

---

## 📂 Project Structure
src/
└── main/
└── java/
└── …
---

## 🎯 Goal

The goal of this project is to build a production-style backend system
with clean architecture and proper Git history.
