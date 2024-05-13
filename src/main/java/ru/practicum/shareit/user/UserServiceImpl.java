package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        log.info("Request to create User = {}", user);
        return userRepository.createUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Requested list of All Users");
        return userRepository.getAllUsers();
    }

    @Override
    public User getUser(long id) {
        log.info("Requested User id = {}", id);
        return userRepository.getUserById(id);
    }

    @Override
    public User updateUser(long id, User user) {
        log.info("Request to update User = {} with id = {}", user, id);
        return userRepository.updateUser(id, user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

}
