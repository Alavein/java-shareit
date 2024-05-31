package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {

    private long id;
    private LocalDate start;
    private LocalDate end;
    @NotNull
    private long item;
    @NotNull
    private long booker;
    private Status status;

}
