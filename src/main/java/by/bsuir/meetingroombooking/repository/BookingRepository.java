package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    Page<Booking> findAllByRoom_Id(Long roomId, Pageable pageable);

    boolean existsByRoom_IdAndStartBeforeAndEndAfter(
            Long roomId,
            LocalDateTime end,
            LocalDateTime start
    );

    boolean existsByUser_IdAndStartBeforeAndEndAfter(
            Long userId,
            LocalDateTime end,
            LocalDateTime start
    );
}
