package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.*;
import by.bsuir.meetingroombooking.repository.BookingRepository;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.time.Duration;

@Service
public class BookingService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AccessService accessService;
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    public BookingService(RoomRepository roomRepository,
                          BookingRepository bookingRepository,
                          UserRepository userRepository,
                          AccessService accessService) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Booking createBooking(
            Long roomId,
            Long userId,
            String title,
            int participantsCount,
            LocalDateTime start,
            LocalDateTime end
    ) {
        log.info("Creating booking: roomId={}, userId={}, title={}, participants={}, start={}, end={}",
                roomId, userId, title, participantsCount, start, end);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.warn("Booking creation failed: room not found, roomId={}", roomId);
                    return new NoSuchElementException("Room not found: " + roomId);
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Booking creation failed: user not found, userId={}", userId);
                    return new NoSuchElementException("user not found: " + userId);
                });

        if (!room.isActive()) {
            log.warn("Booking creation failed: room is inactive, roomId={}", roomId);
            throw new IllegalStateException("room is inactive: " + roomId);
        }

        if (!user.isActive()) {
            log.warn("Booking creation failed: user is inactive, userId={}", userId);
            throw new IllegalStateException("user is inactive: " + userId);
        }

        if (Duration.between(start, end).toHours() > 8) {
            log.warn("Booking creation failed: duration exceeds 8 hours, userId={}, roomId={}", userId, roomId);
            throw new IllegalStateException("booking cannot exceed 8 hours");
        }

        boolean roomConflict = bookingRepository.existsByRoom_IdAndStatusAndStartBeforeAndEndAfter(
                roomId, Status.ACTIVE, end, start
        );

        boolean userConflict = bookingRepository.existsByUser_IdAndStatusAndStartBeforeAndEndAfter(
                userId, Status.ACTIVE, end, start
        );

        if (userConflict) {
            log.warn("Booking creation failed: user conflict, userId={}, start={}, end={}", userId, start, end);
            throw new IllegalStateException("user already has booking in this time");
        }

        if (roomConflict) {
            log.warn("Booking creation failed: room conflict, roomId={}, start={}, end={}", roomId, start, end);
            throw new IllegalStateException("booking conflict for room " + roomId);
        }

        Booking newBooking = new Booking(room, user, title, participantsCount, start, end);

        Booking savedBooking = bookingRepository.save(newBooking);

        log.info("Booking created successfully: bookingId={}, roomId={}, userId={}",
                savedBooking.getId(), roomId, userId);

        return savedBooking;
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long actorId) {
        log.info("Cancelling booking: bookingId={}, actorId={}", bookingId, actorId);

        Booking booking = getBooking(bookingId);

        accessService.requireOwnerOrAdmin(actorId, booking.getUserId());

        booking.cancel();

        log.info("Booking cancelled successfully: bookingId={}, actorId={}", bookingId, actorId);
    }

    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("booking not found: " + bookingId));
    }

    @Transactional(readOnly = true)
    public Page<Booking> listBookingsForRoom(Long roomId, Pageable pageable) {
        return bookingRepository.findAllByRoom_Id(roomId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Booking> listBookingsForUser(Long userId, Long actorId, Pageable pageable) {
        accessService.requireOwnerOrAdmin(actorId, userId);
        accessService.getUserOrThrow(userId);

        return bookingRepository.findAllByUser_Id(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId, Long actorId) {
        Booking booking = getBooking(bookingId);
        accessService.requireOwnerOrAdmin(actorId, booking.getUserId());
        return booking;
    }
}