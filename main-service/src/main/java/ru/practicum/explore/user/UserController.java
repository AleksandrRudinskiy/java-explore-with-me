package ru.practicum.explore.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addNewUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST-create new user {}", userDto);
        return userService.addNewUser(userDto);
    }

    @GetMapping("/admin/users")
    public List<UserDto> getUsersByIds(@RequestParam(required = false) String ids,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        log.info("GET-users by ids = {}", ids);
        return userService.getUsersByIds(ids, from, size);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable long userId) {
        log.info("DELETE user with id {}", userId);
        userService.deleteUserById(userId);
    }

}
