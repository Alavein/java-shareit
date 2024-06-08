package ru.practicum.shareit.user.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class User {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotBlank(groups = {Create.class}, message = "Ошибка. Некорректное имя.")
    private String name;
    @NotNull(groups = {Create.class}, message = "Ошибка. Некорректная почта.")
    @Email(groups = {Update.class, Create.class}, message = "Ошибка. Неверный формат электронной почты")
    @Column(unique = true)
    private String email;
}
