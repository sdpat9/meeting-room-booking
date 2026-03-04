package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final BookingService bookingService;

    public RoomController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Room> listRooms() {
        return bookingService.listRooms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@RequestBody CreateRoomRequest req) {
        return bookingService.createRoom(req.name(),req.capacity(), req.active());
    }

    public record CreateRoomRequest(String name, int capacity, boolean active) {}

    @GetMapping("/{id}")
    public Room getRoom(@PathVariable Long id) {
        return bookingService.getRoom(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        bookingService.deactivateRoom(id);
    }
}
