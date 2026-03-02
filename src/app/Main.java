package app;

import model.Booking;
import model.Room;
import service.BookingService;

import java.util.List;
import java.time.LocalDateTime;

public class Main {

    private static void printBookings(List<Booking> bookings) {
        for (Booking b : bookings) {
            System.out.printf(
                    "id=%d roomId=%d %s..%s status=%s%n",
                    b.getId(),
                    b.getRoomId(),
                    b.getStart(),
                    b.getEnd(),
                    b.getStatus()
            );
        }
    }

    public static void main(String[] args) {
        BookingService service = new BookingService();

        Room r1 = service.createRoom("Omega", 8, true);
        Room r2 = service.createRoom("Sigma", 12, true);

        System.out.println("Rooms: ");

        for (Room r : service.listRooms()) {
            System.out.printf(" id=%d, name=%s, capacity=%d, active=%s%n",
                    r.getId(), r.getName(), r.getCapacity(), r.isActive());
        }

        LocalDateTime s1 = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime e1 = LocalDateTime.of(2026, 3,1,11,0);

        Booking b1 = service.createBooking(r1.getId(), 101L, s1, e1);
        System.out.println("\nCreated Booking: id=" + b1.getId());

        try {
            service.createBooking(
                    r1.getId(),
                    202L,
                    LocalDateTime.of(2026, 3, 1, 10, 30),
                    LocalDateTime.of(2026, 3, 1, 11, 30)
            );
            System.out.println("Error: conflict booking created (should not happen)");
        } catch (IllegalStateException ex) {
                System.out.println("Conflict check: " + ex.getMessage());
        }

        Booking b2 = service.createBooking(
                r2.getId(),
                303L,
                LocalDateTime.of(2026,3,1,10,30),
                LocalDateTime.of(2026,3,1,11,30)
        );

        System.out.println("Created booking in another room: " + b2.getId());

        System.out.println("\nBookings for room  " + r1.getId() + ":");
        printBookings(service.listBookingsForRoom(r2.getId()));

        service.cancelBooking(b1.getId());
        System.out.println("Cancelled booking: id + " + b1.getId());

        Booking b3 = service.createBooking(
                r1.getId(),
                404L,
                LocalDateTime.of(2026, 3, 1, 10, 30),
                LocalDateTime.of(2026, 3, 1, 11, 30)
        );
        System.out.println("Created booking after cancel: " + b3.getId());

        System.out.println("\nBookings for room" + r1.getId() + " after cancel:");
        printBookings(service.listBookingsForRoom(r1.getId()));

    }
}
