package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoRequest {

    private long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private long itemId;

}
