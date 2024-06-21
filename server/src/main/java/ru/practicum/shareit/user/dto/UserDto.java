package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private Integer id;
    @NotBlank(groups = {Create.class}, message = "Ошибка. Некорректное имя.")
    private String name;
    @NotBlank(groups = { Create.class }, message = "Ошибка. Некорректная почта.")
    @Email(groups = { Update.class, Create.class }, message = "Ошибка. Неверный формат электронной почты")
    private String email;
}