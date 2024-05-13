package ru.practicum.shareit.request.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {

    private long id;
    private String description;
    private long requestor;
    private LocalDate created;

}
