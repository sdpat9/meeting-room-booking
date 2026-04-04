package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
            select r
            from Room r
            where r.active = true
              and not exists (
                  select b.id
                  from Booking b
                  where b.room = r
                    and b.status = by.bsuir.meetingroombooking.model.Status.ACTIVE
                    and b.start < :end
                    and b.end > :start
              )
            """)
    List<Room> findAvailableRooms(LocalDateTime start, LocalDateTime end);


    @Query("""
            select r
            from Room r
            where r.active = true
              and r.capacity >= :capacity
              and not exists (
                  select b.id
                  from Booking b
                  where b.room = r
                    and b.status = by.bsuir.meetingroombooking.model.Status.ACTIVE
                    and b.start < :end
                    and b.end > :start
              )
            """)
    List<Room> findAvailableRooms(LocalDateTime start, LocalDateTime end, Integer capacity);
}
