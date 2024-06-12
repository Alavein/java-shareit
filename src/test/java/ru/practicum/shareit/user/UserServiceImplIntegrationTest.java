package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private Integer userId = 1;

    @BeforeEach
    void beforeEach() {
        User user = new User(1, "Имя", "mail@yandex.ru");
        userId = userRepository.save(user).getId();
    }

    @Test
    void getAll() {
        List<UserDto> actualDtoList = userService.getAllUsers(0, 10);

        assertEquals(1, actualDtoList.size());
    }

    @Test
    void getUserById() {
        UserDto userDto = userService.getUserById(userId);

        assertEquals(userId, userDto.getId());
        assertEquals("mail@yandex.ru", userDto.getEmail());
        assertEquals("Имя", userDto.getName());
    }

    @Test
    void addUser() {
        UserDto userDto = new UserDto(1, "Имя2", "mail2@yandex.ru");

        UserDto userDto1 = userService.createUser(userDto);

        assertEquals(1, userDto.getId());
        assertEquals("mail2@yandex.ru", userDto.getEmail());
        assertEquals("Имя2", userDto.getName());
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto(1, "Имя3", "mail2@yandex.ru");
        UserDto userDto1 = userService.updateUser(userDto, userId);

        assertEquals("mail2@yandex.ru", userDto.getEmail());
        assertEquals("Имя3", userDto.getName());
    }

    @Test
    void delete() {
        userService.deleteUser(userId);
        List<UserDto> usersDto = userService.getAllUsers(0, 10);

        assertEquals(List.of(), usersDto);
    }
}
