package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    public User createUser(User user);

    public List<User> getAllUsers();

    public User getUserById(long id);

    public User updateUser(long id, User user);

    public void deleteUser(long id);
}
