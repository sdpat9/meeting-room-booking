package by.bsuir.meetingroombooking.model;

public class Room {
    private Long id;
    private String name;
    private int capacity;
    private boolean active;

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

    public void setId(Long id) {
        this.id = id;
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
