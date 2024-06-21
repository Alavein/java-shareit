package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingStatusPresentation {

    ALL,

    CURRENT,

    FUTURE,

    PAST,

    REJECTED,

    WAITING;

    public static Optional<BookingStatusPresentation> from(String stringState) {
        for (BookingStatusPresentation state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
