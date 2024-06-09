package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;
    @NotBlank(groups = {Create.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}