package ru.practicum.shareit.user;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExists;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Primary
@Component
public class InMemoryUserRepository implements UserRepository {
    private long id = 1;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (!checkIfExists(user)) {
            user.setId(getNextId());
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UserAlreadyExists(String.format("Ошибка. Пользователь с адресом почты = %s не найден.", user.getEmail()));
        }
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public User updateUser(long id, User user) {
        if (users.containsKey(id)) {
            if (users.get(id).getEmail().equals(user.getEmail())) {
                if (user.getName() != null) {
                    users.get(id).setName(user.getName());
                }
            } else {
                if (!checkIfExists(user)) {
                    if (user.getName() != null)
                        users.get(id).setName(user.getName());
                    if (user.getEmail() != null)
                        users.get(id).setEmail(user.getEmail());
                } else {
                    throw new DataNotFoundException(
                            String.format("Ошибка. Пользователь с адресом почты = %s не найден.", user.getEmail()));
                }
            }
            return users.get(id);
        } else {
            throw new DataNotFoundException(String.format("Ошибка. Пользователь с id = %d не найден.", id));
        }
    }

    private boolean checkIfExists(User user) {
        if (user.getEmail() == null)
            return false;
        List<String> existingEmails = users.values().stream().map(u -> u.getEmail()).collect(Collectors.toList());
        for (String email : existingEmails) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    private long getNextId() {
        return id++;
    }
}
