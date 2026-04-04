package by.bsuir.meetingroombooking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(name = "idx_booking_room_time", columnList = "room_id, start_time, end_time")
        }
)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    private String title;
    private int participantsCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    private static final Duration MIN_DURATION = Duration.ofMinutes(15);

    protected Booking() {
    }

    public Booking(Room room, User user, String title, int participantsCount, LocalDateTime start, LocalDateTime end) {

        if (room == null) throw new IllegalArgumentException("room is required");
        if (user == null) throw new IllegalArgumentException("user is required");
        if (start == null || end == null) throw new IllegalArgumentException("start/end is required");

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("start cannot be in the past");
        }

        if (Duration.between(start, end).compareTo(MIN_DURATION) < 0) {
            throw new IllegalArgumentException("booking duration must be at least " + MIN_DURATION.toMinutes() + " minutes");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }

        if (participantsCount <= 0) {
            throw new IllegalArgumentException("participants count must be positive");
        }

        if (participantsCount > room.getCapacity()) {
            throw new IllegalArgumentException("participants count is exceeds room capacity");
        }

        this.room = room;
        this.user = user;
        this.title = title;
        this.participantsCount = participantsCount;
        this.start = start;
        this.end = end;
        this.createdAt = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    public boolean overlaps(Booking other) {
        if (other == null) return false;
        if (this.status != Status.ACTIVE || other.status != Status.ACTIVE) return false;
        if (!this.room.getId().equals(other.room.getId())) return false;

        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }

    public void cancel() {
        if (this.status != Status.ACTIVE) {
            throw new IllegalStateException("only active rooms can be cancelled");
        }
        this.status = Status.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return room.getId();
    }

    public String getTitle() {
        return title;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public Status getStatus() {
        return status;
    }

    public Room getRoom() {
        return room;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getUserId() {
        return user.getId();
    }
 }
