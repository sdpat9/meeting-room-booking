package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping("/room/{roomId}")
    public List<Booking> listBookingsForRoom(@PathVariable Long roomId) {
        return service.listBookingsForRoom(roomId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody CreateBookingRequest req) {
        return service.createBooking(
                req.roomId,
                req.userId(),
                req.start,
                req.end
        );
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable Long bookingId) {
        service.cancelBooking(bookingId);
    }

    public record CreateBookingRequest(
            Long roomId,
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    ) {}
}
