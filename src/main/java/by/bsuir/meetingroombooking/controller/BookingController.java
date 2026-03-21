package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.BookingResponse;
import by.bsuir.meetingroombooking.mapper.BookingMapper;
import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.service.BookingService;
import by.bsuir.meetingroombooking.dto.CreateBookingRequest;
import by.bsuir.meetingroombooking.dto.BookingResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping("/room/{roomId}")
    public Page<BookingResponse> listBookingsForRoom(
            @PathVariable Long roomId,
            Pageable pageable) {
        return service.listBookingsForRoom(roomId, pageable)
                .map(BookingMapper::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest req) {
        Booking booking = service.createBooking(
                req.roomId(),
                req.userId(),
                req.start(),
                req.end()
        );
        return BookingMapper.toResponse(booking);
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable Long bookingId) {
        service.cancelBooking(bookingId);
    }
}
