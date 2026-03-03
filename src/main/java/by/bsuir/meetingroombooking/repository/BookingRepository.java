package by.bsuir.meetingroombooking.repository;

import com.sun.source.tree.LambdaExpressionTree;
import by.bsuir.meetingroombooking.model.Booking;

import java.util.Optional;
import java.util.List;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findAll();
}
