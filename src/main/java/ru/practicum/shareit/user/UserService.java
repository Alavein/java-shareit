package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(Integer page, Integer size);

    UserDto getUserById(Integer id);

    UserDto updateUser(UserDto userDto, Integer id);

    UserDto createUser(UserDto userDto);

    void deleteUser(Integer id);

}