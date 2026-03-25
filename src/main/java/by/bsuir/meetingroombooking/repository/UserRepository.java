package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
