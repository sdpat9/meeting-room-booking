package service;

import model.Booking;
import model.Room;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BookingService {
    private final Map<Long, Room> rooms = new HashMap<>();
    private final Map<Long, Booking> bookings = new HashMap<>();

    private final AtomicLong roomIdSeq = new AtomicLong(1);
    private final AtomicLong bookingIdSeq = new AtomicLong(1);

    public Room createRoom(String name, int capacity, boolean active) {
        long id = roomIdSeq.getAndIncrement();
        Room room = new Room(name, capacity, active);
        room.setId(id);
        rooms.put(id, room);
        return room;
    }

    public Room getRoom(long roomId) {
        Room room = rooms.get(roomId);
        if (room == null) throw new NoSuchElementException("room not found: " + roomId);
        return room;
    }

    public List<Room> listRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Booking createBooking(long roomId, long userId, LocalDateTime start, LocalDateTime end) {
        Room room = getRoom(roomId);
        if (!room.isActive()) throw new IllegalStateException("room is inactive: " + roomId);

        Booking newBooking = new Booking(roomId, userId, start, end);

        for (Booking existing : bookings.values()) {
            Long exRoomId = existing.getRoomId();
            if (exRoomId == null || exRoomId != roomId) continue; // другая комната
            if (newBooking.overlaps(existing)) {
                throw new IllegalStateException("booking conflict for room " + roomId);
            }
        }

        long id = bookingIdSeq.getAndIncrement();
        newBooking.setId(id);
        bookings.put(id, newBooking);
        return newBooking;
    }

    public void cancelBooking(long bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) throw new NoSuchElementException("booking not found: " + bookingId);
        booking.cancel();
    }

    public List<Booking> listBookingsForRoom(long roomId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings.values()) {
            Long bRoomId = b.getRoomId();
            if (bRoomId != null && bRoomId == roomId) {
                result.add(b);
            }
        }
        result.sort(Comparator.comparing(Booking::getStart));
        return result;
    }
}