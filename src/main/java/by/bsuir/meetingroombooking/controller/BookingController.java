package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.BookingResponse;
import by.bsuir.meetingroombooking.dto.PagedResponse;
import by.bsuir.meetingroombooking.dto.RoomResponse;
import by.bsuir.meetingroombooking.mapper.BookingMapper;
import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.service.BookingService;
import by.bsuir.meetingroombooking.dto.CreateBookingRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping("/room/{roomId}")
    public PagedResponse<BookingResponse> listBookingsForRoom(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "start") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (size > 50) {
            throw new IllegalArgumentException("page size must not exceed 50");
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Booking> bookingPage = service.listBookingsForRoom(roomId, pageable);

        return new PagedResponse<>(
                bookingPage.getContent().stream()
                        .map(BookingMapper::toResponse)
                        .toList(),
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages()
        );

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

    @GetMapping("/available")
    public List<RoomResponse> findAvailableRooms(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return service.findAllAvailAbleRooms(start, end)
                .stream()
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getName(),
                        room.getCapacity(),
                        room.isActive()
                ))
                .toList();
    }
}
