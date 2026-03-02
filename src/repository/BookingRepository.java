package repository;

import com.sun.source.tree.LambdaExpressionTree;
import model.Booking;

import java.util.Optional;
import java.util.List;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findAll();
}
