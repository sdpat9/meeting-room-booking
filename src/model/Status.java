package model;

public enum Status {
    ACTIVE,
    CANCELLED,
    COMPLETED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
