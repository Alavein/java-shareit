package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(u -> {
                    if (userDto.getEmail() != null)
                        u.setEmail(userDto.getEmail());
                    if (userDto.getName() != null)
                        u.setName(userDto.getName());
                    return UserMapper.toUserDto(userRepository.save(u));
                })
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

}