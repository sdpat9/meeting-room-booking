package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.*;
import by.bsuir.meetingroombooking.repository.BookingRepository;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;
import java.time.Duration;

@Service
public class BookingService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingService(RoomRepository roomRepository,
                          BookingRepository bookingRepository,
                          UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Room createRoom(String name, int capacity, boolean active) {
        Room room = new Room(name, capacity, active);
        roomRepository.save(room);
        return room;
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found: " + userId));
    }

    @Transactional(readOnly = true)
    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public Booking createBooking(
            Long roomId,
            Long userId,
            String title,
            int participantsCount,
            LocalDateTime start,
            LocalDateTime end) {
        Room room = getRoom(roomId);
        User user = getUser(userId);

        if (!room.isActive()) {
            throw new IllegalStateException("room is inactive: " + roomId);
        }

        if (!user.isActive()) {
            throw new IllegalStateException("user is inactive: " + userId);
        }

        if (Duration.between(start, end).toHours() > 8) {
            throw new IllegalStateException("booking cannot exceed 8 hours");
        }

        Booking newBooking = new Booking(
                room,
                user,
                title,
                participantsCount,
                start,
                end);

        boolean roomConflict = bookingRepository.existsByRoom_IdAndStatusAndStartBeforeAndEndAfter(
                roomId,
                Status.ACTIVE,
                end,
                start
        );

        boolean userConflict = bookingRepository.existsByUser_IdAndStatusAndStartBeforeAndEndAfter(
                userId,
                Status.ACTIVE,
                end,
                start
        );

        if (userConflict) {
            throw new IllegalStateException("user already has booking in this time");
        }

        if (roomConflict) {
            throw new IllegalStateException("booking conflict for room " + roomId);
        }

        bookingRepository.save(newBooking);
        return newBooking;
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("booking not found: " + bookingId));
        booking.cancel();
    }

    @Transactional(readOnly = true)
    public Page<Booking> listBookingsForRoom(Long roomId, Pageable pageable) {
        return bookingRepository.findAllByRoom_Id(roomId, pageable);
    }

    @Transactional
    public void deactivateRoom(Long roomId, Long adminId) {
        User admin = getUser(adminId);
        requireAdmin(admin);

        Room room = getRoom(roomId);
        room.setActive(false);
    }

    @Transactional
    public User createUser(String name, String email, boolean active, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("email is already in use: " + email);
        }

        User user = new User(name, email, active, role);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deactivateUser(Long userId, Long adminId) {
        User admin = getUser(adminId);
        requireAdmin(admin);

        User user = getUser(userId);
        user.setActive(false);
    }

    @Transactional(readOnly = true)
    public Page<Booking> listBookingsForUser(Long userId, Pageable pageable) {
        getUser(userId);
        return bookingRepository.findAllByUser_Id(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("booking not found: " + bookingId));
    }

    @Transactional
    public Room updateRoom(Long roomId, String name, int capacity, boolean active, Long adminId) {
        User admin = getUser(adminId);
        requireAdmin(admin);

        Room room = getRoom(roomId);
        room.update(name, capacity, active);
        return room;
    }

    @Transactional
    public User updateUser(Long userId, String name, String email, boolean active, Role role, Long adminId) {
        User admin = getUser(adminId);
        requireAdmin(admin);

        User user = getUser(userId);

        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new IllegalStateException("email is already in use: " + email);
        }

        user.update(name, email, active, role);
        return user;
    }

    @Transactional(readOnly = true)
    public List<Room> findAvailableRooms(LocalDateTime start, LocalDateTime end, Integer capacity) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end is required");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }

        if (capacity != null && capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }

        if (capacity == null) {
            return roomRepository.findAvailableRooms(start, end);
        }

        return roomRepository.findAvailableRooms(start, end, capacity);
    }

    private void requireAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("admin role is required");
        }
    }
}