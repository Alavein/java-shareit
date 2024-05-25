package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExists;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        for (User user : userRepository.getAllUsers()) {
            if (userDto.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExists("Ошибка. Этот адрес электронной почты уже используется");
            }
        }

        User user = UserMapper.fromUserDto(userDto);

        log.info("Создание нового пользователя = {}", user);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.getAllUsers()) {
            users.add(UserMapper.toUserDto(user));
        }
        log.info("Вывод информации обо всех пользователях.");
        return users;
    }

    @Override
    public UserDto getUserById(long id) {
        if (userRepository.getUserById(id) == null) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        }

        log.info("Вывод информации о пользователе с id = {}", id);
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = userRepository.getUserById(id);

        if (user == null) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден");
        }

        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().equals(user.getEmail())) {
                for (User us : userRepository.getAllUsers()) {
                    if (userDto.getEmail().equals(us.getEmail())) {
                        if (!Objects.equals(userDto.getId(), us.getId())) {
                            throw new UserAlreadyExists("Ошибка. Этот адрес электроенной почты уже используется");
                        }
                    }
                }
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        log.info("Обноваление данных о пользователе = {} с id = {}", user, id);
        return UserMapper.toUserDto(userRepository.updateUser(id, user));
    }

    @Override
    public void deleteUser(long id) {
        if (userRepository.getUserById(id) == null) {
            throw new DataNotFoundException("Ошибка. Пользователь не найден.");
        }

        userRepository.deleteUser(id);
    }

}
