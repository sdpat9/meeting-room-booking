package by.bsuir.meetingroombooking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;
import java.time.Duration;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDateTime start;
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    private Status status;

    private static final Duration MIN_DURATION = Duration.ofMinutes(15);

    protected Booking() {
    }

    public Booking(Long roomId, Long userId, LocalDateTime start, LocalDateTime end) {

        if (roomId == null) throw new IllegalArgumentException("roomId is required");
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (start == null || end == null) throw new IllegalArgumentException("start/end is required");

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("start cannot be in the past");
        }

        if (Duration.between(start, end).compareTo(MIN_DURATION) < 0) {
            throw new IllegalArgumentException("booking duration  must be at least " + MIN_DURATION.toMinutes() + " minutes");
        }

        this.roomId = roomId;
        this.userId = userId;
        this.start = start;
        this.end = end;
        this.status = Status.ACTIVE;
    }

    public boolean overlaps(Booking other) {
        if (other == null) return false;
        if (this.status != Status.ACTIVE || other.status != Status.ACTIVE) return false;
        if (!this.roomId.equals(other.roomId)) return false;

        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }

    public void cancel() {
        this.status = Status.CANCELLED;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
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

 }
