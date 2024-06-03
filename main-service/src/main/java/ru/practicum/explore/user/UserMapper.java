package ru.practicum.explore.user;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.dto.UserShortDto;

@UtilityClass
public class UserMapper {

    public static UserDto convertToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public static User convertToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getName()
        );
    }

    public static UserShortDto convertToUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

}
