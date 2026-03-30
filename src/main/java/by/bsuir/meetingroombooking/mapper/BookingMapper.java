package by.bsuir.meetingroombooking.mapper;

import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.dto.BookingResponse;

public class BookingMapper {

    public static BookingResponse toResponse(Booking booking){
        return new BookingResponse(
                booking.getId(),
                booking.getRoomId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getCreatedAt(),
                booking.getStatus()
        );
    }
}
