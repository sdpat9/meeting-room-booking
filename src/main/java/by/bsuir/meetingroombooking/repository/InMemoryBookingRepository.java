package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.Booking;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Repository
public class InMemoryBookingRepository implements BookingRepository{
    private final Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }
}
