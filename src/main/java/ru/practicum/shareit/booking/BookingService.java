package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;

import java.util.List;


public interface BookingService {

    BookingDto createBooking(BookingDtoAdd bookingDto, Integer userId);

    BookingDto updateBooking(Integer bookingId, Integer userId, Boolean approved);

    BookingDto getBooking(Integer bookingId, Integer userId);

    List<BookingDto> getBookings(String state, Integer userId);

    List<BookingDto> getBookingFromOwner(String state, Integer userId);
}
