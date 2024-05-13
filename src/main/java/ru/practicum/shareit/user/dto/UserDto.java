package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {

    private long id;
    private String name;
    @NotNull(groups = {Create.class}, message = "Invalid email")
    @Email(groups = {Update.class, Create.class}, message = "Invalid email format")
    private String email;
}