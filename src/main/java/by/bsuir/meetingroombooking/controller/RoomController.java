package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.RoomResponse;
import by.bsuir.meetingroombooking.dto.UpdateRoomRequest;
import by.bsuir.meetingroombooking.mapper.RoomMapper;
import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.dto.CreateRoomRequest;
import by.bsuir.meetingroombooking.dto.PagedResponse;
import by.bsuir.meetingroombooking.service.RoomService;
import by.bsuir.meetingroombooking.security.CustomUserDetails;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rooms")
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public PagedResponse<RoomResponse> listRooms(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (size > 50) {
            throw new IllegalArgumentException("page size must not exceed 50");
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Room> roomPage = roomService.listRooms(
                active,
                name,
                pageable
        );

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateRoomRequest req) {
        Room room = roomService.createRoom(
                req.name(),
                req.capacity(),
                req.active(),
                currentUser.getId()
        );
        return RoomMapper.toResponse(room);
    }

    @GetMapping("/{id}")
    public RoomResponse getRoom(@PathVariable Long id) {
        Room room = roomService.getRoom(id);
        return RoomMapper.toResponse(room);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public RoomResponse updateRoom(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateRoomRequest req
            ) {
        Room room = roomService.updateRoom(
                id,
                req.name(),
                req.capacity(),
                req.active(),
                currentUser.getId()
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

        Page<Room> roomPage = roomService.findAvailableRooms(start, end, capacity, pageable);

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

    @GetMapping("/recommend")
    public RoomResponse recommendRoom(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam int participants
    ) {
        Room room = roomService.recommendRoom(start, end, participants);
        return RoomMapper.toResponse(room);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        roomService.deactivateRoom(id, currentUser.getId());
    }
}
