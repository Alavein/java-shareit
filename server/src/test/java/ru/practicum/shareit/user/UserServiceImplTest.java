package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById() {
        User user = new User(1, "Имя", "mail@yandex.ru");

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));

        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto1 = userService.getUserById(user.getId());

        assertEquals(userDto, userDto1);
    }

    @Test
    void getUserByIdObjectNotFoundException() {
        Integer userId = 1000;

        Mockito.when(userRepository.findById(Mockito.anyInt()))
                .thenThrow(new DataNotFoundException("Ошибка. Пользователь не найден."));

        assertThrows(DataNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void addUserDto() {
        User user = new User(1, "Имя", "mail@yandex.ru");

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto1 = userService.createUser(userDto);

        assertEquals(userDto, userDto1);
    }

    @Test
    void addEmailNotValid() {
        UserDto userDto = new UserDto(1, "Имя", null);
        Mockito.when(userRepository.save(Mockito.any())).thenThrow(ValidationException.class);

        assertThrows(ValidationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void updateUser() {
        Integer userId = 1;
        User user = new User(1, "Имя", "mail@yandex.ru");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = new UserDto(1, "Имя2", "mail@yandex.ru");
        UserDto userDto11 = userService.updateUser(userDto, userId);

        assertEquals(userDto, userDto11);
    }

    @Test
    void delete() {
        User user = new User(1, "Имя", "mail@yandex.ru");

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1);
    }
}
