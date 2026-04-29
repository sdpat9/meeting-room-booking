package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.model.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    Page<Booking> findAllByRoom_Id(Long roomId, Pageable pageable);

    boolean existsByRoom_IdAndStatusAndStartBeforeAndEndAfter(
            Long roomId,
            Status status,
            LocalDateTime end,
            LocalDateTime start
    );

    boolean existsByUser_IdAndStatusAndStartBeforeAndEndAfter(
            Long userId,
            Status status,
            LocalDateTime end,
            LocalDateTime start
    );

    Page<Booking> findAllByUser_Id(Long userId, Pageable pageable);
}
