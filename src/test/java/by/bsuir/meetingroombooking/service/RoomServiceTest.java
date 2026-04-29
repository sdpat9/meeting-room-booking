package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    private RoomRepository roomRepository;
    private AccessService accessService;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        accessService = mock(AccessService.class);

        roomService = new RoomService(roomRepository, accessService);
    }

    @Test
    void recommendRoom_returnsBestFitRoom() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        Room roomB = new Room("Room B", 8, true);
        Room roomC = new Room("Room C", 12, true);

        when(roomRepository.findAvailableRooms(
                eq(start),
                eq(end),
                eq(6),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(roomC, roomB)));

        Room result = roomService.recommendRoom(start, end, 6);

        assertEquals("Room B", result.getName());
        assertEquals(8, result.getCapacity());
    }

    @Test
    void recommendRoom_noSuitableRoom_throwsException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        when(roomRepository.findAvailableRooms(
                eq(start),
                eq(end),
                eq(100),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of()));

        assertThrows(Exception.class, () ->
                roomService.recommendRoom(start, end, 100)
        );
    }

    @Test
    void createRoom_nonAdmin_forbidden() {
        doThrow(new IllegalStateException("admin role is required"))
                .when(accessService)
                .requireAdmin(2L);

        assertThrows(IllegalStateException.class, () ->
                roomService.createRoom("Room A", 10, true, 2L)
        );

        verify(roomRepository, never()).save(any());
    }

    @Test
    void createRoom_admin_success() {
        Room room = new Room("Room A", 10, true);

        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room result = roomService.createRoom("Room A", 10, true, 1L);

        assertEquals("Room A", result.getName());
        assertEquals(10, result.getCapacity());
        assertTrue(result.isActive());

        verify(accessService).requireAdmin(1L);
        verify(roomRepository).save(any(Room.class));
    }
}