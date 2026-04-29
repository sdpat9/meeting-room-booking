package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Role;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AccessService {

    private final UserRepository userRepository;

    public AccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found: " + userId));
    }

    public void requireAdmin(Long adminId) {
        User admin = getUserOrThrow(adminId);

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("admin role is required");
        }
    }

    public void requireOwnerOrAdmin(Long actorId, Long ownerId) {
        User actor = getUserOrThrow(actorId);

        boolean isAdmin = actor.getRole() == Role.ADMIN;
        boolean isOwner = ownerId.equals(actorId);

        if (!isAdmin && !isOwner) {
            throw new IllegalStateException("access denied");
        }
    }
}