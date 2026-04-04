package by.bsuir.meetingroombooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private Role role;

    protected User(){
    }

    public User(String name, String email, boolean active, Role role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }

        if (role == null) {
            throw new IllegalArgumentException("role is required");
        }

        this.name = name;
        this.email = email;
        this.active = active;
        this.role = role;
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        this.name = name;
    }

    public void changeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
    }

    public void changeRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("role is required");
        }
        this.role = role;
    }

    public void update(String name, String email, boolean active, Role role) {
        rename(name);
        changeEmail(email);
        setActive(active);
        changeRole(role);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }
}
