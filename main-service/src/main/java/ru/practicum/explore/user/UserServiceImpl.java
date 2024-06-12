package ru.practicum.explore.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.common.IncorrectRequestException;
import ru.practicum.explore.common.NotFoundException;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        String email = userDto.getEmail();
        String[] parts = email.split("@");
        String[] emailDomainParts = parts[1].split("\\.");
        String localPart = parts[0];
        String domainPart = emailDomainParts[0];
        if (localPart.length() > 64 || domainPart.length() > 63) {
            throw new IncorrectRequestException("werfwefwefw");
        }
        User user = userRepository.save(UserMapper.convertToUser(userDto));
        log.info("Created New user with userId = {}", user.getId());
        return UserMapper.convertToUserDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(String ids, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Параметр from не должен быть меньше 1");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (ids == null) {
            return userRepository.findAll(page).stream()
                    .map(UserMapper::convertToUserDto)
                    .collect(Collectors.toList());
        } else {
            String[] arrayStrIds = ids.split(",");
            List<Long> userIds = new ArrayList<>();
            for (String strId : arrayStrIds) {
                userIds.add(Long.parseLong(strId));
            }
            List<User> users = userRepository.selectUsers(userIds, page);
            return users.stream()
                    .map(UserMapper::convertToUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }


}
