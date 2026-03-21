package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.RoomResponse;
import by.bsuir.meetingroombooking.mapper.RoomMapper;
import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import by.bsuir.meetingroombooking.service.BookingService;
import by.bsuir.meetingroombooking.dto.CreateRoomRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final BookingService bookingService;

    public RoomController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<RoomResponse> listRooms() {
        return bookingService.listRooms()
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest req) {
        Room room = bookingService.createRoom(req.name(),req.capacity(), req.active());
        return RoomMapper.toResponse(room);
    }

    @GetMapping("/{id}")
    public RoomResponse getRoom(@PathVariable Long id) {
        Room room = bookingService.getRoom(id);
        return RoomMapper.toResponse(room);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        bookingService.deactivateRoom(id);
    }
}
