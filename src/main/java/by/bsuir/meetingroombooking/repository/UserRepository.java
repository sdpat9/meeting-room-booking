package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String id);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<User> findByEmail(String email);
}
