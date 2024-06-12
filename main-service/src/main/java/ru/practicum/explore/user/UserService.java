package ru.practicum.explore.user;

import ru.practicum.explore.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserDto userDto);

    List<UserDto> getUsersByIds(String ids, int from, int size);

    void deleteUserById(long userId);

}
