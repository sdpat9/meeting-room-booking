package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.BookingRepository;
import by.bsuir.meetingroombooking.repository.RoomRepositry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingService {
    private final RoomRepositry roomRepositry;
    private final BookingRepository bookingRepository;

    private final AtomicLong roomIdSeq = new AtomicLong(1);
    private final AtomicLong bookingIdSeq = new AtomicLong(1);

    public BookingService(RoomRepositry roomRepositry, BookingRepository bookingRepository) {
        this.roomRepositry = roomRepositry;
        this.bookingRepository = bookingRepository;
    }

    public Room createRoom(String name, int capacity, boolean active) {
        long id = roomIdSeq.getAndIncrement();
        Room room = new Room(name, capacity, active);
        room.setId(id);
        roomRepositry.save(room);
        return room;
    }

    public Room getRoom(long roomId) {
        return roomRepositry.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    }

    public List<Room> listRooms() {
        return roomRepositry.findAll();
    }

    public Booking createBooking(long roomId, long userId, LocalDateTime start, LocalDateTime end) {
        Room room = getRoom(roomId);
        if (!room.isActive()) throw new IllegalStateException("room is inactive: " + roomId);

        Booking newBooking = new Booking(roomId, userId, start, end);

        for (Booking existing : bookingRepository.findAll()) {
            Long exRoomId = existing.getRoomId();
            if (exRoomId == null || exRoomId != roomId) continue;
            if (newBooking.overlaps(existing)) {
                throw new IllegalStateException("booking conflict for room " + roomId);
            }
        }

        long id = bookingIdSeq.getAndIncrement();
        newBooking.setId(id);
        bookingRepository.save(newBooking);
        return newBooking;
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("booking not found: +" + bookingId));
        booking.cancel();
        bookingRepository.save(booking);
    }

    public List<Booking> listBookingsForRoom(long roomId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookingRepository.findAll()) {
            Long bRoomId = b.getRoomId();
            if (bRoomId != null && bRoomId == roomId) {
                result.add(b);
            }
        }
        result.sort(Comparator.comparing(Booking::getStart));
        return result;
    }

    public void deactivateRoom(Long roomId) {
        Room room = getRoom(roomId);
        room.setActive(false);
    }
}