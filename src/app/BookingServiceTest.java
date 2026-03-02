package app;

import model.Status;
import org.junit.jupiter.api.Test;
import repository.BookingRepository;
import repository.InMemoryBookingRepository;
import repository.InMemoryRoomRepository;
import service.BookingService;

import model.Room;
import model.Booking;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    @Test
    void createRoom_shouldAssignIdAndStoreRoom() {
        BookingService service = new BookingService(
                new InMemoryRoomRepository(),
                new InMemoryBookingRepository()
        );

        Room room = service.createRoom("Omega", 10, true);

        assertTrue(room.getId() > 0);
        assertEquals("Omega", room.getName());
        assertEquals(10, room.getCapacity());
        assertTrue(room.isActive());

        Room fromService = service.getRoom(room.getId());
        assertEquals(room.getId(), fromService.getId());
    }

    @Test
    void getRoom_shouldThrowIfNotEqual() {
        BookingService service = new BookingService(
                new InMemoryRoomRepository(),
                new InMemoryBookingRepository()
        );

        assertThrows(NoSuchElementException.class, () -> service.getRoom(999));
    }

    @Test
    void createBooking_shouldThrowOverlapInSomeRoom() {
        BookingService service = new BookingService(
                new InMemoryRoomRepository(),
                new InMemoryBookingRepository()

        );
        Room room = service.createRoom("Omega", 10, true);

        service.createBooking(
                room.getId(),
                1L,
                LocalDateTime.of(2026, 4,1,10,0),
                LocalDateTime.of(2026,4,1,11,0)
        );

        assertThrows(IllegalStateException.class, () ->
                service.createBooking(
                        room.getId(),
                        2L,
                        LocalDateTime.of(2026, 4,1,10,30),
                        LocalDateTime.of(2026,4,1,11, 30)
                )
        );
    }

    @Test
    void createBooking_shouldThrowIfRoomInactive() {
        BookingService service = new BookingService(
                new InMemoryRoomRepository(),
                new InMemoryBookingRepository()
        );
        Room room = service.createRoom("Closed", 10, false);

        assertThrows(IllegalStateException.class, () ->
            service.createBooking(
                    room.getId(),
                    1L,
                    LocalDateTime.of(2026,3,1,10,0),
                    LocalDateTime.of(2026,3,1,11,0)
            )
        );
    }

    @Test
    void createBooking_shouldAllowSameTimeInDifferentRooms() {
        BookingService service = new BookingService(
                new InMemoryRoomRepository(),
                new InMemoryBookingRepository()
        );
        Room r1 = service.createRoom("A", 10, true);
        Room r2 = service.createRoom("B", 10, true);

        Booking b1 = service.createBooking(
                r1.getId(),
                1L,
                LocalDateTime.of(2026,4,1,10,0),
                LocalDateTime.of(2026,4,1,11,0)
        );

        Booking b2 = service.createBooking(
                r2.getId(),
                2L,
                LocalDateTime.of(2026,4,1,10,0),
                LocalDateTime.of(2026,4,1,11,0)
        );

        assertTrue(b2.getId() > 0);
        assertEquals(r2.getId(), b2.getRoomId());
    }

    @Test
    void cancelBooking_shouldChangeStatus() {
        BookingService service = new BookingService(
                new InMemoryRoomRepository(),
                new InMemoryBookingRepository()
        );
        Room room = service.createRoom("Omega", 10, true);

        Booking b = service.createBooking(
                room.getId(),
                1L,
                LocalDateTime.of(2026, 4,1,10,0),
                LocalDateTime.of(2026,4,1,11,0)
        );

        service.cancelBooking(b.getId());

        assertEquals(Status.CANCELLED, b.getStatus());
    }

    @Test
    void listBookingsForRoom_shouldReturnSortedByStart() {
        BookingService service = new BookingService(
                new InMemoryRoomRepository(),
                new InMemoryBookingRepository()
        );
        Room room = service.createRoom("Omega", 10, true);

        Booking b1 = service.createBooking(
                room.getId(),
                1L,
                LocalDateTime.of(2026, 4,1,12,0),
                LocalDateTime.of(2026,4,1,13,0)
        );

        Booking b2 = service.createBooking(
                room.getId(),
                2L,
                LocalDateTime.of(2026,4,1,10,0),
                LocalDateTime.of(2026,4,1,11,0)
        );

        List<Booking> list = service.listBookingsForRoom(room.getId());
        assertEquals(2, list.size());
        assertEquals(b2.getId(), list.get(0).getId());
        assertEquals(b1.getId(), list.get(1).getId());
    }
}
