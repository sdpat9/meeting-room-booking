package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    List<Booking> findAllByRoomId(Long roomId);
}
