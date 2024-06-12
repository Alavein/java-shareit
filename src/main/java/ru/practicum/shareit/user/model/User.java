package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(groups = {Create.class}, message = "Ошибка. Некорректное имя.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Ошибка. Некорректная почта.")
    @Email(groups = {Update.class, Create.class}, message = "Ошибка. Неверный формат электронной почты")
    @Column(unique = true)
    private String email;
}
