package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.*;
import by.bsuir.meetingroombooking.repository.BookingRepository;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private AccessService accessService;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        roomRepository = mock(RoomRepository.class);
        userRepository = mock(UserRepository.class);
        accessService = mock(AccessService.class);

        bookingService = new BookingService(
                roomRepository,
                bookingRepository,
                userRepository,
                accessService
        );
    }

    @Test
    void createBooking_success() {
        Room room = new Room("Room A", 10, true);
        User user = new User("Sergey", "sergey@example.com", true, Role.USER);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByRoom_IdAndStatusAndStartBeforeAndEndAfter(
                1L, Status.ACTIVE, end, start
        )).thenReturn(false);
        when(bookingRepository.existsByUser_IdAndStatusAndStartBeforeAndEndAfter(
                2L, Status.ACTIVE, end, start
        )).thenReturn(false);
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking(
                1L,
                2L,
                "Team meeting",
                5,
                start,
                end
        );

        assertNotNull(result);
        assertEquals("Team meeting", result.getTitle());
        assertEquals(5, result.getParticipantsCount());
        assertEquals(Status.ACTIVE, result.getStatus());

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_roomConflict_throwsException() {
        Room room = new Room("Room A", 10, true);
        User user = new User("Sergey", "sergey@example.com", true, Role.USER);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByRoom_IdAndStatusAndStartBeforeAndEndAfter(
                1L, Status.ACTIVE, end, start
        )).thenReturn(true);
        when(bookingRepository.existsByUser_IdAndStatusAndStartBeforeAndEndAfter(
                2L, Status.ACTIVE, end, start
        )).thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking(
                        1L,
                        2L,
                        "Team meeting",
                        5,
                        start,
                        end
                )
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_userConflict_throwsException() {
        Room room = new Room("Room A", 10, true);
        User user = new User("Sergey", "sergey@example.com", true, Role.USER);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByRoom_IdAndStatusAndStartBeforeAndEndAfter(
                1L, Status.ACTIVE, end, start
        )).thenReturn(false);
        when(bookingRepository.existsByUser_IdAndStatusAndStartBeforeAndEndAfter(
                2L, Status.ACTIVE, end, start
        )).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking(
                        1L,
                        2L,
                        "Team meeting",
                        5,
                        start,
                        end
                )
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_ownerOrAdmin_success() {
        Room room = new Room("Room A", 10, true);
        User user = new User("Sergey", "sergey@example.com", true, Role.USER);
        ReflectionTestUtils.setField(user, "id", 2L);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        Booking booking = new Booking(room, user, "Team meeting", 5, start, end);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(1L, 2L);

        assertEquals(Status.CANCELLED, booking.getStatus());
        verify(accessService).requireOwnerOrAdmin(2L, 2L);
    }

    @Test
    void cancelBooking_notOwnerAndNotAdmin_forbidden() {
        Room room = new Room("Room A", 10, true);
        User user = new User("Sergey", "sergey@example.com", true, Role.USER);
        ReflectionTestUtils.setField(user, "id", 2L);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        Booking booking = new Booking(room, user, "Team meeting", 5, start, end);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        doThrow(new IllegalStateException("access denied"))
                .when(accessService)
                .requireOwnerOrAdmin(3L, 2L);

        assertThrows(IllegalStateException.class, () ->
                bookingService.cancelBooking(1L, 3L)
        );

        assertEquals(Status.ACTIVE, booking.getStatus());
    }
}