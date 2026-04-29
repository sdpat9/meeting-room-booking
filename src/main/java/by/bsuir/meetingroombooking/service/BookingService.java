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
    private final AccessService accessService;

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
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found: " + userId));

        if (!room.isActive()) {
            throw new IllegalStateException("room is inactive: " + roomId);
        }

        if (!user.isActive()) {
            throw new IllegalStateException("user is inactive: " + userId);
        }

        if (Duration.between(start, end).toHours() > 8) {
            throw new IllegalStateException("booking cannot exceed 8 hours");
        }

        boolean roomConflict = bookingRepository.existsByRoom_IdAndStatusAndStartBeforeAndEndAfter(
                roomId, Status.ACTIVE, end, start
        );

        boolean userConflict = bookingRepository.existsByUser_IdAndStatusAndStartBeforeAndEndAfter(
                userId, Status.ACTIVE, end, start
        );

        if (userConflict) {
            throw new IllegalStateException("user already has booking in this time");
        }

        if (roomConflict) {
            throw new IllegalStateException("booking conflict for room " + roomId);
        }

        Booking newBooking = new Booking(
                room,
                user,
                title,
                participantsCount,
                start,
                end
        );

        return bookingRepository.save(newBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long actorId) {
        Booking booking = getBooking(bookingId);

        accessService.requireOwnerOrAdmin(actorId, booking.getUserId());

        booking.cancel();
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
}