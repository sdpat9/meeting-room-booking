package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.RoomResponse;
import by.bsuir.meetingroombooking.dto.UpdateRoomRequest;
import by.bsuir.meetingroombooking.mapper.RoomMapper;
import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import by.bsuir.meetingroombooking.service.BookingService;
import by.bsuir.meetingroombooking.dto.CreateRoomRequest;

import by.bsuir.meetingroombooking.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
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
    public RoomResponse createRoom(
            @RequestParam Long adminId,
            @Valid @RequestBody CreateRoomRequest req) {
        Room room = bookingService.createRoom(
                req.name(),
                req.capacity(),
                req.active(),
                adminId
        );
        return RoomMapper.toResponse(room);
    }

    @GetMapping("/{id}")
    public RoomResponse getRoom(@PathVariable Long id) {
        Room room = bookingService.getRoom(id);
        return RoomMapper.toResponse(room);
    }

    @PutMapping("/{id}")
    public RoomResponse updateRoom(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @Valid @RequestBody UpdateRoomRequest req
            ) {
        Room room = bookingService.updateRoom(
                id,
                req.name(),
                req.capacity(),
                req.active(),
                adminId
        );
        return RoomMapper.toResponse(room);
    }

    @GetMapping("/available")
    public PagedResponse<RoomResponse> findAllAvailableRooms(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "capacity") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (size > 50) {
            throw new IllegalArgumentException("page size must not exceed 50");
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Room> roomPage = bookingService.findAvailableRooms(start, end, capacity, pageable);

        return new PagedResponse<>(
                roomPage.getContent().stream()
                        .map(RoomMapper::toResponse)
                        .toList(),
                roomPage.getNumber(),
                roomPage.getSize(),
                roomPage.getTotalElements(),
                roomPage.getTotalPages()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        bookingService.deactivateRoom(id, adminId);
    }
}
