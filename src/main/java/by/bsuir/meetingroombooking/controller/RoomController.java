package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.RoomResponse;
import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import by.bsuir.meetingroombooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import by.bsuir.meetingroombooking.dto.CreateRoomRequest;

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
                .map(room -> new RoomResponse(
                        room.getId(),
                        room.getName(),
                        room.getCapacity(),
                        room.isActive()
                ))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@RequestBody CreateRoomRequest req) {
        Room room = bookingService.createRoom(req.name(),req.capacity(), req.active());

        return new RoomResponse (
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.isActive()
        );
    }

    @GetMapping("/{id}")
    public RoomResponse getRoom(@PathVariable Long id) {
        Room room = bookingService.getRoom(id);

        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.isActive()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        bookingService.deactivateRoom(id);
    }
}
