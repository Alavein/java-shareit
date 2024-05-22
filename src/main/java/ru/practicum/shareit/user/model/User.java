package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class User {

    private long id;
    @NotBlank(groups = {Create.class}, message = "Ошибка. Некорректное имя.")
    private String name;
    @NotBlank(groups = { Create.class }, message = "Ошибка. Некорректная почта.")
    @Email(groups = { Update.class, Create.class }, message = "Ошибка. Неверный формат электронной почты")
    private String email;
}
