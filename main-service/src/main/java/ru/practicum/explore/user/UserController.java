package ru.practicum.explore.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.participation_request.ParticipationRequest;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.participation_request.dto.EventRequestStatusUpdateResult;
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

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequest addParticipationRequest(@PathVariable long userId,
                                                        @RequestParam(defaultValue = "0") Long eventId) {
        log.info("POST add new participation request from userId {} to eventId {}", userId, eventId);
        return userService.addParticipationRequest(userId, eventId);
    }

    @GetMapping("/users/{userId}/events")
    public List<Event> addEvent(@PathVariable long userId,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("GET user events from userId: {}", userId);
        return userService.getUserEvents(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequestStatus(@PathVariable long userId,
                                                             @PathVariable long eventId,
                                                             @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("PATCH /users/{}/events/{}/requests with body {}", userId, eventId, request);
        return userService.patchRequestStatus(userId, eventId, request);
    }

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequest> getUsersRequests(@PathVariable long userId) {
        log.info("GET /users/{}/requests", userId);
        return userService.getUsersRequests(userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequest> getCurrentUsersRequests(@PathVariable long userId,
                                                              @PathVariable long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return userService.getCurrentUsersRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequest canceledRequest(@PathVariable long userId,
                                                @PathVariable long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return userService.canceledRequest(userId, requestId);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable long userId) {
        log.info("DELETE user with id {}", userId);
        userService.deleteUserById(userId);
    }

}
