package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoomControllerTest {

    private MockMvc mockMvc;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomService = mock(RoomService.class);
        RoomController roomController = new RoomController(roomService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(roomController)
                .setCustomArgumentResolvers(new TestAuthenticationPrincipalResolver())
                .build();
    }

    @Test
    void listRooms_success() throws Exception {
        Room roomA = new Room("Room A", 4, true);
        Room roomB = new Room("Room B", 8, true);

        when(roomService.listRooms(
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(
                List.of(roomA, roomB),
                PageRequest.of(0, 10),
                2
        ));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Room A"))
                .andExpect(jsonPath("$.content[0].capacity").value(4))
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.content[1].name").value("Room B"))
                .andExpect(jsonPath("$.content[1].capacity").value(8))
                .andExpect(jsonPath("$.content[1].active").value(true))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getRoom_success() throws Exception {
        Room room = new Room("Room A", 10, true);

        when(roomService.getRoom(1L)).thenReturn(room);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Room A"))
                .andExpect(jsonPath("$.capacity").value(10))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createRoom_success() throws Exception {
        Room room = new Room("Room A", 10, true);

        when(roomService.createRoom(
                eq("Room A"),
                eq(10),
                eq(true),
                eq(1L)
        )).thenReturn(room);

        mockMvc.perform(post("/api/rooms")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Room A",
                                  "capacity": 10,
                                  "active": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Room A"))
                .andExpect(jsonPath("$.capacity").value(10))
                .andExpect(jsonPath("$.active").value(true));

        verify(roomService).createRoom("Room A", 10, true, 1L);
    }

    @Test
    void updateRoom_success() throws Exception {
        Room room = new Room("Updated Room", 12, true);

        when(roomService.updateRoom(
                eq(1L),
                eq("Updated Room"),
                eq(12),
                eq(true),
                eq(1L)
        )).thenReturn(room);

        mockMvc.perform(put("/api/rooms/1")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Updated Room",
                                  "capacity": 12,
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.capacity").value(12))
                .andExpect(jsonPath("$.active").value(true));

        verify(roomService).updateRoom(1L, "Updated Room", 12, true, 1L);
    }

    @Test
    void deactivateRoom_success() throws Exception {
        doNothing().when(roomService).deactivateRoom(1L, 1L);

        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNoContent());

        verify(roomService).deactivateRoom(1L, 1L);
    }

    @Test
    void findAvailableRooms_success() throws Exception {
        Room roomA = new Room("Room A", 4, true);
        Room roomB = new Room("Room B", 8, true);

        when(roomService.findAvailableRooms(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(4),
                any(PageRequest.class)
        )).thenReturn(new PageImpl<>(
                List.of(roomA, roomB),
                PageRequest.of(0, 10),
                2
        ));

        mockMvc.perform(get("/api/rooms/available")
                        .param("start", "2026-04-10T10:00:00")
                        .param("end", "2026-04-10T12:00:00")
                        .param("capacity", "4")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "capacity")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Room A"))
                .andExpect(jsonPath("$.content[1].name").value("Room B"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void recommendRoom_success() throws Exception {
        Room room = new Room("Room B", 8, true);

        when(roomService.recommendRoom(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(6)
        )).thenReturn(room);

        mockMvc.perform(get("/api/rooms/recommend")
                        .param("start", "2026-04-10T10:00:00")
                        .param("end", "2026-04-10T12:00:00")
                        .param("participants", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Room B"))
                .andExpect(jsonPath("$.capacity").value(8))
                .andExpect(jsonPath("$.active").value(true));
    }
}