package by.bsuir.meetingroombooking.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@EntityListeners(AuditingEntityListener.class)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int capacity;
    private boolean active;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    protected Room() {
    }

    public Room(String name, int capacity, boolean active) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.name = name;
        this.capacity = capacity;
        this.active = active;
    }

    public void changeCapacity(int newCapacity) {
        if (newCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = newCapacity;
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name must not be blank");
        }
        this.name = name;
    }

    public void update(String name, int capacity, boolean active) {
        rename(name);
        changeCapacity(capacity);
        setActive(active);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "model.Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", active=" + active +
                '}';
    }
}
