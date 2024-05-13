package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {

    private long id;
    private String name;
    @NotNull(groups = { Create.class }, message = "Ошибка. Некорректная почта.")
    @Email(groups = { Update.class, Create.class }, message = "Ошибка. Неверный формат электронной почты")
    private String email;
}
