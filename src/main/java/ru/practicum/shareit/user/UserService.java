package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    public UserDto createUser(UserDto userDto);

    public List<UserDto> getAllUsers();

    public UserDto getUserById(long id);

    public UserDto updateUser(long id, UserDto userDto);

    public void deleteUser(long id);
}
