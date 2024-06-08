package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse updateBooking(long bookingId, long ownerId, boolean approved);

    BookingDtoResponse getBooking(long bookingId, long userId);

    List<BookingDtoResponse> getBookings(long ownerId, String state);

    List<BookingDtoResponse> getBookingFromOwner(long ownerId, String state);
}
