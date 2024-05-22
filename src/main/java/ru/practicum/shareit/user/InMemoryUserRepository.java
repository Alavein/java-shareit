package ru.practicum.shareit.user;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;


@Primary
@Component
public class InMemoryUserRepository implements UserRepository {
    private long id = 0;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        long counter = ++id;
        user.setId(id);
        users.put(counter, user);
        return user;
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
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    private long getNextId() {
        return id++;
    }
}
